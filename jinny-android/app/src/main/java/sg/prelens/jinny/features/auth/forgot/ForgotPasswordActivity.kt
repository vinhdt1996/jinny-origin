package sg.prelens.jinny.features.auth.forgot

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.internals.AnkoInternals.addView
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.isValidEmail
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.setUpHideSoftKeyboard
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/13/18<br>.
 */
class ForgotPasswordActivity : BaseActivity(), View.OnClickListener, View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = this@ForgotPasswordActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)
        return false
    }

    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    override fun isBackPressed(): Boolean {
        return true
    }

    override fun replaceFragmentId(): Int {
        return 0
    }

    override fun isFullScreen(): Boolean {
        return false
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_forgot_password
    }

    private lateinit var dialogInterface: DialogInterface

    private var roomDB: RoomDB? = null

    override fun init() {
        super.init()
        setTitle(getString(R.string.forgot_password_main), 0)
        setBackgroundToolbar(0)

        roomDB = RoomDB.newInstance(this)

        ivBack?.setOnClickListener(this)
        rlForgot_Password?.setOnTouchListener(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_forgot_password, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }

        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_forgot_password, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_forgot_password, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        forgotPasswordViewModel = getForgotPasswordModel()
        btnSubmitForgotPassword.setOnClickListener {
            if (edtEmail.text.isNullOrEmpty()) {
//                toast(R.string.err_email_empty)
                showDialog(getString(R.string.err_email_empty), null)
                return@setOnClickListener
            }
            if (!edtEmail.text.toString().isValidEmail()) {
//                toast(R.string.err_email_invalid)
                showDialog(getString(R.string.err_email_invalid), null)
                return@setOnClickListener
            }
            hideKeyboard()
            forgotPasswordViewModel.setForgotPassword(edtEmail.text.toString())
            clearEmail()
        }
        forgotPasswordViewModel.forgotPasswordLiveData.observe(this, Observer {
            it?.let {
                //                toast(capitalizeFirstLetter(it.message) ?: "Error!!!")
                val email = "user_email: " + edtEmail.text.toString()
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_forgot_password, email, this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_forgot_password, description = email)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                showDialog(it.message.toString(), 0)
//                onBackPressed()
            }
        })
        forgotPasswordViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    //                toast(capitalizeFirstLetter(it).toString())
                    showDialog(it, null)
                }
            }
        })
    }

    private fun clearEmail() {
        edtEmail.text.clear()
        edtEmail.requestFocus()
    }

    private fun getForgotPasswordModel(): ForgotPasswordViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@ForgotPasswordActivity)
                            .getForgotPasswordRepository()
                    @Suppress("UNCHECKED_CAST")
                    return ForgotPasswordViewModel(repo) as T
                }
            })[ForgotPasswordViewModel::class.java]

    fun capitalizeFirstLetter(original: String?): String? {
        return if (original == null || original.length == 0) {
            original
        } else original.substring(0, 1).toUpperCase() + original.substring(1)
    }

    private fun showDialog(message: String?, flag: Int?) {
        dialogInterface = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialogCustom()
                    } else {
                        hideDialogCustom()
                        onBackPressed()
                    }
                    isCancelable = false
                }
                view.tvContent?.text = message
            }
        }.show()
    }

    private fun hideDialogCustom() {
        dialogInterface.cancel()
    }
}