package sg.prelens.jinny.features.addbarcode

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.os.Trace
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import androidx.work.State
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_add_barcode.*
import kotlinx.android.synthetic.main.activity_barcode.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_barcode.*
import org.jetbrains.anko.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.setUpHideSoftKeyboard
import sg.prelens.jinny.features.membershipdetail.MembershipDetailActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class AddBarcodeActivity : AppCompatActivity(), View.OnClickListener {
    private var thumb: String? = null
    private lateinit var glide: RequestManager
    private var id: Int? = null
    private var name: String? = null
    private var serialNumber: String? = null
    private lateinit var addBarcodeViewModel: AddBarcodeViewModel
    private lateinit var barCodeDialog: DialogInterface
    private var roomDB: RoomDB? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_barcode)
        roomDB = RoomDB.newInstance(this)

        addBarcodeViewModel = getBarcodeModel()
        thumb = intent.extras["thumb"] as? String
        id = intent.extras["id"] as? Int
        name = intent.extras["name"] as? String
        serialNumber = intent.extras["serialNumber"] as? String
        if (thumb == null) {
            throw IllegalStateException("Thumb id could not be null")
        }
        if (id == null) {
            throw IllegalStateException("Id id could not be null")
        }
        if (name == null) {
            throw IllegalStateException("Id id could not be null")
        }

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_add_nobarcode, description = getString(R.string.membership_add_info, name, id?.toString()))
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_add_nobarcode, getString(R.string.membership_add_info, name, id?.toString()), this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_add_nobarcode, description = getString(R.string.membership_add_info, name, id?.toString()))
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        glide = Glide.with(this)
        ivThumb.loadFromUrl(thumb, glide)
        ivBackBarcode?.setOnClickListener(this)
        tvNoBarcode?.setOnClickListener(this)
        btnDone?.setOnClickListener(this)
        setUpHideSoftKeyboard(container)
        if (!serialNumber.isNullOrEmpty()) {
            edtSerial.setText(serialNumber.toString())
            edtSerial.isEnabled = false
        }
        addBarcodeViewModel.addBarcodeLiveData.observe(this, Observer {
            it?.run {
                val membership = it
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.membership_add, getString(R.string.membership_info, name, it.result?.code, it.result?.id?.toString()), this@AddBarcodeActivity)?.observe(this@AddBarcodeActivity, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.membership_add, description = getString(R.string.membership_info, name, membership.result?.code, membership.result?.id?.toString()))
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this@AddBarcodeActivity)
                    }
                })

                barCodeDialog = alert {
                    customView {
                        val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                        addView(view, null)
                        view.tvContent.apply {
                            text = getString(R.string.membership_added)
                        }
                        view.btnOk.setOnClickListener {
                            hideBarCodeDialog()
                        }
                        view.btnCancel.apply {
                            visibility = View.GONE
                        }
                    }
                    isCancelable = false
                    val name = it.result?.merchant?.name ?: ""
                    val id = it.result?.id ?: 0
                    onCancelled {
                        TaskStackBuilder.create(this@AddBarcodeActivity)
                                .addNextIntentWithParentStack(intentFor<MembershipDetailActivity>("id" to id, "name" to name))
                                .startActivities()
                    }
                }.show()

            }
        })
        addBarcodeViewModel.errorLiveData.observe(this, Observer {
            it?.printStackTrace()
            if (it != null) {
                it.parseMessage().let {
                    Log.d("", it)
                    showDialog(it)
                }
            }
        })
    }

    private fun showDialog(message: String?) {
        Trace.beginSection("show dialog")
        barCodeDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    hideBarCodeDialog()
                }
                view.tvContent?.text = message
            }
        }.show()
        Trace.endSection()
    }

    private fun hideBarCodeDialog() {
        barCodeDialog.cancel()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBackBarcode -> {
                onBackPressed()
            }
            R.id.btnDone -> {
                hideKeyboard()
                addBarcodeViewModel.setBarcode(edtSerial?.text.toString(), id!!)
            }
        }
    }

    private fun getBarcodeModel(): AddBarcodeViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@AddBarcodeActivity).addBarcodeRepository()
                    @Suppress("UNCHECKED_CAST")
                    return AddBarcodeViewModel(repo) as T
                }
            })[AddBarcodeViewModel::class.java]
}