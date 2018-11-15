package sg.prelens.jinny.features.auth

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.*
import sg.prelens.jinny.features.auth.forgot.ForgotPasswordActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.settings.SettingPrefs
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.vinova.trackingtool.model.EventType

class SignInFragment : Fragment(), View.OnTouchListener {
    private val loadingProgress: Dialog by lazy {
        Dialog(this@SignInFragment.requireContext())
    }
    private lateinit var dialogInterface: DialogInterface

    private var roomDB: RoomDB? = null


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        return false
    }

    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var signInViewModel: SignInViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_sign_in, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        roomDB = RoomDB.newInstance(this@SignInFragment.requireContext())

        if (!TrackingHelper.hasConnection(this@SignInFragment.requireContext())) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_login, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this@SignInFragment.requireContext())
        }

        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_login, "", requireContext())?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_login, description = null)
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this@SignInFragment.requireContext())
            }
        })

        signInViewModel = getSignInModel()
        btnSignIn.setOnClickListener {
            if (tilEmail.editText?.text.isNullOrEmpty() && tilPass.editText?.text.isNullOrEmpty()) {
//                activity?.toast("Please input email and password")
                showDialog("Please input email and password")
                return@setOnClickListener
            }
            if (tilEmail.editText?.text.isNullOrEmpty()) {
//                activity?.toast(R.string.err_email_empty)
                showDialog(getString(R.string.err_email_empty))
                return@setOnClickListener
            }
            if (!tilEmail.editText?.text.toString().isValidEmail()) {
//                activity?.toast(R.string.err_email_invalid)
                showDialog(getString(R.string.err_email_invalid))
                return@setOnClickListener
            }
            if (tilPass.editText?.text.isNullOrEmpty()) {
//                activity?.toast(R.string.err_password_empty)
                showDialog(getString(R.string.err_password_empty))
                return@setOnClickListener
            }
            loadingProgress.showLoading()
            activity?.hideKeyboard()
            signInViewModel.setLogin(tilEmail.editText?.text.toString(), tilPass.editText?.text.toString())
        }
        signInViewModel.userLiveData.observe(this, Observer {
            it?.let {

                loadingProgress.hideLoading()
                activity?.saveUserPref(it)
                SettingPrefs(this@SignInFragment.requireContext())
                        .oldFCMToken = FirebaseInstanceId.getInstance().token ?: ""
                (context?.applicationContext as JinnyApplication).reloadCurUser()

                val user_id = "user_id: " + it.id
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_login, user_id, requireContext())?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_login, description = user_id)
                        TrackingHelper.setDB(default, roomDB
                                ?: return@Observer, this@SignInFragment.requireContext())
                    }
                })

                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("page", 1)
                startActivity(intent)
                activity?.finish()
            }
        })
        ctlSign_in?.setOnTouchListener(this)
        signInViewModel.errorLiveData.observe(this, Observer {
            log("On sign in error ${it?.parseMessage()}")
            loadingProgress.hideLoading()
            if (it != null) {
                it.parseMessage().let {
                    showDialog(it)
                }
            }
        })
        tvForgotPassword.setOnClickListener {
            context?.startActivity<ForgotPasswordActivity>()
            //launchActivity<ForgotPasswordActivity> { }
        }
    }

    private fun showDialog(message: String?) {
        dialogInterface = this@SignInFragment.requireActivity().alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.tvContent?.text = message
                view.btnOk?.setOnClickListener {
                    hideDialog()
                }
                isCancelable = false
            }
        }.show()
    }

    private fun hideDialog() {
        dialogInterface.cancel()
    }

    private fun getSignInModel(): SignInViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@SignInFragment.requireContext())
                            .getAuthRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SignInViewModel(repo) as T
                }

            })[SignInViewModel::class.java]
}