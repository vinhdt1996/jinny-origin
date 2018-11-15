package sg.prelens.jinny.features.barcode

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult

import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_barcode.*
import org.jetbrains.anko.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.features.addbarcode.AddBarcodeActivity
import sg.prelens.jinny.features.addbarcode.AddBarcodeViewModel
import sg.prelens.jinny.features.membershipdetail.MembershipDetailActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType


class BarcodeActivity : AppCompatActivity(), View.OnClickListener {
    private var thumb: String? = null
    private var id: Int? = null
    private var name: String? = null
    private lateinit var glide: RequestManager
    private lateinit var addBarcodeViewModel: AddBarcodeViewModel
    private lateinit var barCodeDialog: DialogInterface

    companion object {
        const val CAMERA_REQUEST_CODE = 0x2
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_barcode)

        roomDB = RoomDB.newInstance(this)

        addBarcodeViewModel = getBarcodeModel()

        id = intent.extras["id"] as? Int
        thumb = intent.extras["thumb"] as? String
        name = intent.extras["name"] as? String
        if (id == null) {
            throw IllegalStateException("Id id could not be null")
        }
        if (thumb == null) {
            throw IllegalStateException("Thumb id could not be null")
        }
        if (name == null) {
            throw IllegalStateException("Thumb id could not be null")
        }

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_add_barcode, description = getString(R.string.membership_add_info, name, id?.toString()))
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_add_barcode, getString(R.string.membership_add_info, name, id?.toString()), this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_add_barcode, description = getString(R.string.membership_add_info, name, id?.toString()))
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        glide = Glide.with(this)
        ivThumb.loadFromUrl(thumb, glide)
        ivBackBarcode?.setOnClickListener(this)
        tvNoBarcode?.setOnClickListener(this)
        barCodeScannerView.setStatusText("")
        addBarcodeViewModel.addBarcodeLiveData.observe(this@BarcodeActivity, android.arch.lifecycle.Observer {
            it?.run {

                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.membership_add, getString(R.string.membership_info, name, it.result?.code, it.result?.id?.toString()), this@BarcodeActivity)?.observe(this@BarcodeActivity, Observer {

                })

                barCodeDialog = alert {
                    customView {
                        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                        addView(view, null)
                        view.tvContent.apply { text = getString(R.string.membership_added) }
                        view.btnOk.setOnClickListener({ this@BarcodeActivity.hideBarCodeDialog() })
                        view.btnCancel.visibility = View.GONE
                    }
                    isCancelable = false
                    val name = it.result?.merchant?.name ?: ""
                    val id = it.result?.id ?: 0
                    onCancelled {
                        TaskStackBuilder.create(this@BarcodeActivity)
                                .addNextIntentWithParentStack(
                                        intentFor<MembershipDetailActivity>("id" to id, "name" to name))
                                .startActivities()
                    }
                }.show()
            }
        })

        addBarcodeViewModel.errorLiveData.observe(this@BarcodeActivity, android.arch.lifecycle.Observer {
            it?.run {
                barCodeDialog = alert {
                    customView {
                        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                        addView(view, null)
                        view.tvContent.apply { text = it.message.toString() }
                        view.btnOk.setOnClickListener({ this@BarcodeActivity.hideBarCodeDialog() })
                        view.btnCancel.visibility = View.GONE
                    }
                    isCancelable = false
                    onCancelled { barCodeScannerView.resume() }
                }.show()
            }
        })

        if (checkCameraHardware())
            setUpPermission()
    }


    private fun setUpCamera() {
        barCodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}

            override fun barcodeResult(result: BarcodeResult) {
                barCodeScannerView.pause()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBackBarcode ->
                onBackPressed()
            R.id.tvNoBarcode -> {
                startActivity<AddBarcodeActivity>("id" to id, "thumb" to thumb, "name" to name)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun setUpPermission() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED)
            makeRequestPermission()
        else
            setUpCamera()
    }

    private fun checkCameraHardware(): Boolean = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)


    private fun makeRequestPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                toast("We need camera permission to scan barcode")
                showDialog("We need camera permission to scan barcode")
            } else {
                setUpCamera()
            }
        }
    }

    private fun hideBarCodeDialog() {
        barCodeDialog.cancel()
    }

    override fun onResume() {
        super.onResume()
        barCodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        barCodeScannerView.pause()
    }

    private fun showDialog(message: String?) {
        barCodeDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    hideDialog()
                }
                view.tvContent?.text = message
            }
        }.show()
    }

    private fun hideDialog() {
        barCodeDialog.cancel()
    }

    private fun getBarcodeModel(): AddBarcodeViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@BarcodeActivity).addBarcodeRepository()
                    @Suppress("UNCHECKED_CAST")
                    return AddBarcodeViewModel(repo) as T
                }
            })[AddBarcodeViewModel::class.java]
}