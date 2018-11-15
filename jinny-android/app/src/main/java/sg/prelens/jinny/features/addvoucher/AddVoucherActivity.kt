package sg.prelens.jinny.features.addvoucher

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import kotlinx.android.synthetic.main.activity_qrcode.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_qrcode.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class AddVoucherActivity : AppCompatActivity() {
    private lateinit var glide: RequestManager
    private lateinit var resultDialog: DialogInterface
    private val addVoucherViewModel: AddVoucherViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@AddVoucherActivity).addQrCodeRepository()
                return AddVoucherViewModel(repo) as T
            }
        })[AddVoucherViewModel::class.java]
    }

    companion object {
        const val CAMERA_REQUEST_CODE = 0x2
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_qrcode)
        ivBackQRCode.setOnClickListener { onBackPressed() }
        glide = Glide.with(this)
        setTitle(R.string.add_voucher)

        roomDB = RoomDB.newInstance(this)

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_add, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_add, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_add, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        addVoucherViewModel.addQrCodeLiveData.observe(this@AddVoucherActivity, android.arch.lifecycle.Observer {
            it?.run {
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deals_add, getString(R.string.deal_info, it.result.merchant_name, it.result.id), this@AddVoucherActivity)?.observe(this@AddVoucherActivity, Observer {

                })
                resultDialog = alert {
                    customView {
                        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                        addView(view, null)
                        view.tvContent.apply { text = getString(R.string.voucher_added) }
                        view.btnOk.setOnClickListener({ this@AddVoucherActivity.hideBarCodeDialog() })
                        view.btnCancel.visibility = View.GONE
                    }
                    isCancelable = false
                    onCancelled {
                        onBackPressed()
                    }
                }.show()
            }
        })
        addVoucherViewModel.errorLiveData.observe(this@AddVoucherActivity, android.arch.lifecycle.Observer {
            it?.run {
                resultDialog = alert {
                    customView {
                        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                        addView(view, null)
                        view.tvContent.apply { text = it.message.toString() }
                        view.btnOk.setOnClickListener({ this@AddVoucherActivity.hideBarCodeDialog() })
                        view.btnCancel.visibility = View.GONE
                    }
                    isCancelable = false
                    onCancelled {
                        qrCodeScannerView.resume()
                    }
                }.show()
            }
        })
        if (checkCameraHardware())
            setUpPermission()

        qrCodeScannerView.setStatusText("")
    }

    private fun setUpCamera() {
//        val formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeForma)
//        qrCodeScannerView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        qrCodeScannerView.decodeContinuous(object : BarcodeCallback {
            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}

            override fun barcodeResult(result: BarcodeResult) {
                addVoucherViewModel.setQrCode(result.text)
                qrCodeScannerView.pause()
            }
        })
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

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.notifyRefreshVoucher()
    }

    private fun checkCameraHardware(): Boolean = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun hideBarCodeDialog() {
        resultDialog.cancel()
    }

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

    override fun onResume() {
        super.onResume()
        qrCodeScannerView.resume()
    }

    override fun onPause() {
        super.onPause()
        qrCodeScannerView.pause()
    }

    private fun showDialog(message: String?) {
        resultDialog = alert {
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
        resultDialog.cancel()
    }

}