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
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_sign_up.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.exts.*
import sg.prelens.jinny.features.auth.skipsignup.SkipSignUpViewModel
import sg.prelens.jinny.features.cashbackinfo.TermsActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.utils.SharePreference
import android.widget.TextView
import android.text.method.LinkMovementMethod
import android.text.Spanned
import android.text.style.ClickableSpan
import android.util.EventLog
import android.widget.Toast
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import sg.prelens.jinny.R.string.privacy
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.features.cashbackinfo.PrivacyActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType


class SignUpFragment : Fragment(), View.OnTouchListener {
    private var devideID: String? = ""
    private val loadingProgress: Dialog by lazy {
        Dialog(this@SignUpFragment.requireContext())
    }

    private lateinit var dialogInterface: DialogInterface

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        return false
    }

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var signUpGuestViewModel: SkipSignUpViewModel
    private var roomDB: RoomDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_sign_up, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        roomDB = RoomDB.newInstance(this@SignUpFragment.requireContext())

        if (!TrackingHelper.hasConnection(this@SignUpFragment.requireContext())) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_signup, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this@SignUpFragment.requireContext())
        }

        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_signup, "", requireContext())?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_signup, description = null)
                TrackingHelper.setDB(default, roomDB
                        ?: return@Observer, this@SignUpFragment.requireContext())
            }
        })

        signUpViewModel = getSignUpModel()
        signUpGuestViewModel = getSkipSignUpModel()
        btnSignUp.setOnClickListener {
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
            if (!ivTaC.isSelected) {
//                activity?.toast(R.string.err_term_unchecked)
                showDialog(getString(R.string.err_term_unchecked))
                return@setOnClickListener
            }



            activity?.hideKeyboard()
            loadingProgress.showLoading()
            signUpViewModel.setLogin(tilEmail.editText?.text.toString(), tilPass.editText?.text.toString())

        }

        skip_signup.setOnClickListener {
            if (!ivTaC.isSelected) {
//                activity?.toast(R.string.err_term_unchecked)
                showDialog(getString(R.string.err_term_unchecked))
                return@setOnClickListener
            }
            if (devideID != null) {
                loadingProgress.showLoading()
                signUpGuestViewModel.setSignUpGuestPassword(devideID + System.currentTimeMillis())
            }
        }

        ctlSign_up?.setOnTouchListener(this)
        ivTaC.setOnClickListener {
            if (!ivTaC.isSelected) {
                ivTaC.isSelected = true
                ivTaC.setImageDrawable(ContextCompat.getDrawable(this@SignUpFragment.requireContext(), R.drawable.check_box_on))
            } else {
                ivTaC.isSelected = false
                ivTaC.setImageDrawable(ContextCompat.getDrawable(this@SignUpFragment.requireContext(), R.drawable.check_box))
            }
        }
        val terms = object : ClickableSpan() {
            override fun onClick(view: View) {
                this@SignUpFragment.requireContext().startActivity<TermsActivity>()
            }
        }

        val privacy = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                this@SignUpFragment.requireContext().startActivity<PrivacyActivity>()
            }
        }
        makeLinks(tvTaC, arrayOf(
                "Terms and Conditions", "Privacy Policy"), arrayOf(terms, privacy))

        signUpViewModel.userLiveData.observe(this, Observer {
            it?.let {
                val user_id = "user_id: " + it.id
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_signup, user_id, requireContext())?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_signup, description = user_id)
                        TrackingHelper.setDB(default, roomDB
                                ?: return@Observer, this@SignUpFragment.requireContext())
                    }
                })
                loadingProgress.hideLoading()
                it.isFirstTime_Membership = true
                it.isFirstTime_CashBack = true
                activity?.saveUserPref(it)
                (context?.applicationContext as JinnyApplication).reloadCurUser()
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("page", 1)
                startActivity(intent)
                activity?.finish()
            }
        })
        signUpViewModel.errorLiveData.observe(this, Observer {
            log("On sign in error ${it?.parseMessage()}")
            if (it != null) {
                it?.parseMessage()?.let {
                    //                activity?.toast(it)
                    showDialog(it)
                }
            }
            loadingProgress.hideLoading()
        })
        devideID = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)

        signUpGuestViewModel.signUpGuestLiveData.observe(this, Observer {
            it?.let {
                val user_id = "user_id: " + it.id
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_skip_signup, user_id, requireContext())?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_skip_signup, description = user_id)
                        TrackingHelper.setDB(default, roomDB
                                ?: return@Observer, this@SignUpFragment.requireContext())
                    }
                })
                it.isFirstTime_Membership = true
                it.isFirstTime_CashBack = true
                SharePreference.getInstance().saveGuestAccount(it)
                FirebaseAnalyticsUtil.putLoginPropertyGuestAnalytics(this@SignUpFragment.requireContext())
                loadingProgress.hideLoading()
                val intent = Intent(activity, MainActivity::class.java)
                intent.putExtra("page", 1)
                startActivity(intent)
                activity?.finish()
            }
        })
        signUpGuestViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    //                activity?.toast(it)
                    showDialog(it)
                }
            }
            loadingProgress.hideLoading()
        })
    }

    fun makeLinks(textView: TextView, links: Array<String>, clickableSpans: Array<ClickableSpan>) {
        val spannableString = SpannableString(textView.text)
        for (i in links.indices) {
            val clickableSpan = clickableSpans[i]
            val link = links[i]

            val startIndexOfLink = textView.text.toString().indexOf(link)
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        // textView.highlightColor = Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun getSignUpModel(): SignUpViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@SignUpFragment.requireContext())
                            .getAuthRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SignUpViewModel(repo) as T
                }

            })[SignUpViewModel::class.java]

    private fun showDialog(message: String?) {
        dialogInterface = this@SignUpFragment.requireActivity().alert {
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

    private fun hideDialog() {
        dialogInterface.cancel()
    }

    private fun getSkipSignUpModel(): SkipSignUpViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@SignUpFragment.requireContext())
                            .getAuthRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SkipSignUpViewModel(repo) as T
                }

            })[SkipSignUpViewModel::class.java]
}
