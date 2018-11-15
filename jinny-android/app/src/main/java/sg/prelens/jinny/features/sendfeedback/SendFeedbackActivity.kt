package sg.prelens.jinny.features.sendfeedback

import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_send_feedback.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import kotlinx.android.synthetic.main.layout_toolbar_detail_normal.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.setUpHideSoftKeyboard
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType


class SendFeedbackActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var dialogInterface: DialogInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_feedback)
        setUpUi()
    }

    private var roomDB: RoomDB? = null

    private fun setUpUi() {
        this.setUpHideSoftKeyboard(container)
        roomDB = RoomDB.newInstance(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_contact_jinny, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.app_contact_jinny, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_contact_jinny, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })
        tvTitle.text = getString(R.string.send_us_feedback)
        tvTopTitle.text = getString(R.string.send_feedback_top_title)
//        tvTopTitle.movementMethod = LinkMovementMethod.getInstance()
//        tvTopTitle.removeUnderline("contact@jinny.com")
        ivBack.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> {
                val send = Intent(Intent.ACTION_SENDTO)

                val uriText = "mailto:" + Uri.encode("hello@myjinny.com") +
                        "?subject=" + Uri.encode(edtSubject?.text?.toString()) +
                        "&body=" + Uri.encode(edtDescription?.text?.toString())
                val uri = Uri.parse(uriText)
                send.data = uri

                try {
                    startActivity(Intent.createChooser(send, "Send mail..."))
                } catch (ex: android.content.ActivityNotFoundException) {
//                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
                    showDialog("There are no email clients installed.")
                }
            }
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    fun showDialog(message: String?) {
        dialogInterface = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    hideDialog()
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    fun hideDialog() {
        dialogInterface.cancel()
    }
}