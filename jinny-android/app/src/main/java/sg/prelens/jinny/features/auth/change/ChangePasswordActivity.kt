package sg.prelens.jinny.features.auth.change

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
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.deleteUserToken
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.features.auth.AuthActivity
import sg.prelens.jinny.features.main.LogoutViewModel
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType
import kotlin.concurrent.thread

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/18/18<br>.
 */
class ChangePasswordActivity : BaseActivity(), View.OnClickListener, View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = this@ChangePasswordActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)
        return false
    }

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var logoutViewModel: LogoutViewModel
    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun replaceFragmentId(): Int {
        return 0
    }

    override fun isFullScreen(): Boolean {
        return false
    }

    override fun isBackPressed(): Boolean {
        return true
    }

    private lateinit var dialogInterface: DialogInterface

    override fun init() {
        super.init()
        setTitle(getString(R.string.change_password), 0)
        setBackgroundToolbar(0)
        ivBack?.setOnClickListener(this)
        lnChangePass?.setOnTouchListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private var roomDB: RoomDB? = null

    override fun initViewModel() {
        super.initViewModel()
        changePasswordViewModel = getChangePasswordModel()
        roomDB = RoomDB.newInstance(this)
        logoutViewModel = deleteAccountModel()
        btnChange.setOnClickListener {
            if (tilCurrentPass.editText?.text.isNullOrEmpty()) {
//                this?.toast(R.string.err_current_password_empty)
                showDialog(getString(R.string.err_current_password_empty), null)
                return@setOnClickListener
            }
            if (tilNewPass.editText?.text.isNullOrEmpty()) {
//                this?.toast(R.string.err_new_password_empty)
                showDialog(getString(R.string.err_new_password_empty), null)
                return@setOnClickListener
            }
            this?.hideKeyboard()
            changePasswordViewModel.setChangePassword(tilCurrentPass.editText?.text.toString(), tilNewPass.editText?.text.toString())
        }
        changePasswordViewModel.changePasswordLiveData.observe(this, Observer {
            it?.let {
                //                toast(it.message.toString())
                val user_id = "user_id: " + getCurrentUser()?.id
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_change_password, user_id, this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_change_password, description = user_id)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                showDialog(it.message.toString(), 0)
//                logOut()
            }
        })
        changePasswordViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    showDialog(it, null)
                }
            }
        })
    }

    private fun logOut() {
        logoutViewModel.logoutAccountLiveData.observe(this@ChangePasswordActivity, Observer {
            it?.let {
                thread(start = true) {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                }
                val user_id = "user_id: " + getCurrentUser()?.id
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_signout, user_id, this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_signout, description = user_id)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                this@ChangePasswordActivity.deleteUserToken()
                (this@ChangePasswordActivity.application as JinnyApplication).currentUser = null
                startActivity<AuthActivity>()
                this@ChangePasswordActivity.finish()

            }
        })
        logoutViewModel.errorLiveData.observe(this@ChangePasswordActivity, Observer {
            it?.parseMessage()?.let {
                //                toast(it)
                showDialog(it, null)
            }
        })
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
                        logOut()
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

    private fun getChangePasswordModel(): ChangePasswordViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@ChangePasswordActivity).changePasswordRepository()
                    @Suppress("UNCHECKED_CAST")
                    return ChangePasswordViewModel(repo) as T
                }
            })[ChangePasswordViewModel::class.java]

    private fun deleteAccountModel(): LogoutViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@ChangePasswordActivity)
                            .deleteAccount()
                    @Suppress("UNCHECKED_CAST")
                    return LogoutViewModel(repo) as T
                }
            })[LogoutViewModel::class.java]
}