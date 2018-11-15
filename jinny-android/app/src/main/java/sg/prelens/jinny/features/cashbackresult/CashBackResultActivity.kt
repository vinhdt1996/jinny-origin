package sg.prelens.jinny.features.cashbackresult

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_cashback_request_result.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail_normal.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.customView
import org.jetbrains.anko.toast
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.vinova.trackingtool.model.EventType
import java.io.File


class CashBackResultActivity : AppCompatActivity() {
    private var imagePath: String? = null
    private var id: String? = null
    private var usersVoucherId: Int? = null
    private var name: String? = null
    private val loadingProgress: Dialog by lazy {
        Dialog(this@CashBackResultActivity)
    }
    private val model: CashBackResultViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@CashBackResultActivity).getCashBackResult()
                return CashBackResultViewModel(repo) as T
            }
        })[CashBackResultViewModel::class.java]
    }

    private var roomDB: RoomDB? = null

    private lateinit var confirmationDialog: DialogInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashback_request_result)
        roomDB = RoomDB.newInstance(this)
        imagePath = intent.extras["file"] as? String
        if (imagePath == null) {
            if (BuildConfig.DEBUG)
                throw IllegalStateException("imagePath could not be null")
            else {
//                toast("Please choose other image")
                showDialog("Please choose other image", 0)
//                onBackPressed()
                this@CashBackResultActivity.finish()
            }
        }

        tvTitle?.text = R.string.cash_back_request.parseResString()
        ivBack.setOnClickListener {
            onBackPressed()
        }

        id = intent.extras["id"] as? String
        if (id == null) {
            throw IllegalStateException("id could not be null")
        }

        usersVoucherId = intent.extras["usersVoucherId"] as? Int
        if (usersVoucherId == null) {
            throw IllegalStateException("usersVoucherId could not be null")
        }

        name = intent.extras["name"] as? String?

        tvTitle.text = R.string.cashback_request.parseResString()
        val imgFile = File(imagePath)
        if (imgFile.exists()) {
            val myBitmap: Bitmap? = BitmapFactory.decodeFile(imgFile.absolutePath)
            ivCashBackResult?.setImageBitmap(imageProcessor(myBitmap
                    ?: return, imgFile.absolutePath))
        }
        model.response.observe(this, Observer {
            TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.deals_detail_submit_cashback, getString(R.string.deal_info, name
                    ?: "", id), this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.deals_detail_submit_cashback, description = getString(R.string.deal_info, name
                            ?: "", id))
                    TrackingHelper.setDB(default, roomDB
                            ?: return@Observer, this)
                }
            })

            confirmationDialog = alert {
                customView {
                    val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                    addView(view, null)
                    view.tvContent.apply {
                        text = it?.message ?: ""
                    }
                    view.btnOk.text = getString(R.string.back_to_my_vouchers)
                    view.btnOk.setOnClickListener {
                        confirmationDialog.cancel()
                        val intent = Intent(this@CashBackResultActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        intent.clearTop()
                        loadingProgress.hideLoading()
                        startActivity(intent)
                    }
                    isCancelable = false
                    view.btnCancel.apply {
                        visibility = View.GONE
                    }
                }
            }.show()
        })
        model.errorLiveData.observe(this, Observer {
            //            if(it != null)
//            Toast.makeText(this@CashBackResultActivity,
//                    it.message, Toast.LENGTH_LONG).show()
            if (it != null) {
                it.parseMessage().let {
                    showDialog(it, 1)
                }
            }
        })
        btnSubmit.setOnClickListener {
            loadingProgress.showLoading()
            model.setUpdatePhoto(id ?: return@setOnClickListener,
                    usersVoucherId ?: return@setOnClickListener,
                    imagePath ?: return@setOnClickListener)
            AppEvent.notifyRefreshCashBackDashBoard(0)
        }
        tvTakeAgain.setOnClickListener {
            onBackPressed()
        }
    }

    private fun imageProcessor(bitmap: Bitmap, photoPath: String): Bitmap {
        val ei = ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 ->
                rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 ->
                rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 ->
                rotateImage(bitmap, 270f)
            else ->
                bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    private fun showDialog(message: String?, flag: Int?) {
        confirmationDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialog()
                    } else {
                        hideDialog()
                        onBackPressed()
                    }
                }
                view.tvContent?.text = message
            }
        }.show()
    }

    private fun hideDialog() {
        loadingProgress.hideLoading()
        confirmationDialog.cancel()
    }
}