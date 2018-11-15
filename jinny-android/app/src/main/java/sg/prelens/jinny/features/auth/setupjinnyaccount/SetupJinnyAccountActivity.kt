package sg.prelens.jinny.features.auth.setupjinnyaccount

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_setup_jinny_account.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.*
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideKeyboard
import sg.prelens.jinny.exts.isValidEmail
import sg.prelens.jinny.exts.parseMessage
import sg.prelens.jinny.exts.saveUserPref
import sg.prelens.jinny.features.cashbackinfo.PrivacyActivity
import sg.prelens.jinny.features.cashbackinfo.TermsActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.EventType

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/26/18<br>.
 */
class SetupJinnyAccountActivity : BaseActivity(), View.OnClickListener, View.OnTouchListener {
    private lateinit var signUpGuestViewModel: SetupJinnyAccountViewModel
    var resultDialog: DialogInterface? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_setup_jinny_account
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

    private var roomDB: RoomDB? = null

    override fun init() {
        super.init()

        roomDB = RoomDB.newInstance(this)

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_setup_account, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }

        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_setup_account, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_setup_account, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        setTitle(getString(R.string.setup_jinny_account), 0)
        setBackgroundToolbar(0)
        ivBack?.setOnClickListener(this)
        signUpGuestViewModel = getSignUpGuestModel()
        btnSignUpGuest.setOnClickListener {
            if (tilEmailGuest.editText?.text.isNullOrEmpty()) {
//                this@SetupJinnyAccountActivity?.toast(R.string.err_email_empty)
                showDialog(getString(R.string.err_email_empty))
                return@setOnClickListener
            }
            if (!tilEmailGuest.editText?.text.toString().isValidEmail()) {
//                this@SetupJinnyAccountActivity?.toast(R.string.err_email_invalid)
                showDialog(getString(R.string.err_email_invalid))
                return@setOnClickListener
            }
            if (tilPassGuest.editText?.text.isNullOrEmpty()) {
//                this@SetupJinnyAccountActivity?.toast(R.string.err_password_empty)
                showDialog(getString(R.string.err_password_empty))
                return@setOnClickListener
            }
            if (!ivTaCGuest.isSelected) {
//                this@SetupJinnyAccountActivity?.toast(R.string.err_term_unchecked)
                showDialog(getString(R.string.err_term_unchecked))
                return@setOnClickListener
            }
            this@SetupJinnyAccountActivity?.hideKeyboard()
            //Click
            signUpGuestViewModel.setSignUpGuest(tilEmailGuest.editText?.text.toString(), tilPassGuest.editText?.text.toString())
        }
        ctlSignUpGuest?.setOnTouchListener(this)
        ivTaCGuest.setOnClickListener {
            if (!ivTaCGuest.isSelected) {
                ivTaCGuest.isSelected = true
                ivTaCGuest.setImageDrawable(ContextCompat.getDrawable(this@SetupJinnyAccountActivity, R.drawable.check_box_on))
            } else {
                ivTaCGuest.isSelected = false
                ivTaCGuest.setImageDrawable(ContextCompat.getDrawable(this@SetupJinnyAccountActivity, R.drawable.check_box))
            }
        }
        val terms = object : ClickableSpan() {
            override fun onClick(view: View) {
                this@SetupJinnyAccountActivity.startActivity<TermsActivity>()
            }
        }

        val privacy = object : ClickableSpan() {
            override fun onClick(widget: View?) {
                this@SetupJinnyAccountActivity.startActivity<PrivacyActivity>()
            }
        }
        makeLinks(tvForgotPasswordGuest, arrayOf(
                "Terms and Conditions", "Privacy Policy"), arrayOf(terms, privacy))
//        tvForgotPasswordGuest.setOnClickListener {
//            if (!ivTaCGuest.isSelected) {
//                ivTaCGuest.isSelected = true
//                ivTaCGuest.setImageDrawable(ContextCompat.getDrawable(this@SetupJinnyAccountActivity, R.drawable.check_box_on))
//            } else {
//                ivTaCGuest.isSelected = false
//                ivTaCGuest.setImageDrawable(ContextCompat.getDrawable(this@SetupJinnyAccountActivity, R.drawable.check_box))
//            }
//            startActivity(intentFor<TermsActivity>())
//        }
        signUpGuestViewModel.signUpGuestLiveData.observe(this@SetupJinnyAccountActivity, Observer {
            resultDialog = alert {
                customView {
                    val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                    addView(view, null)
                    view.tvContent.apply { text = getString(R.string.setup_account_success) }
                    view.btnOk.setOnClickListener({ this@SetupJinnyAccountActivity.hideBarCodeDialog() })
                    view.btnCancel.visibility = View.GONE
                }
                isCancelable = false
                onCancelled {
                    onBackPressed()
                }
            }.show()
        })
        signUpGuestViewModel.errorLiveData.observe(this@SetupJinnyAccountActivity, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    //                this@SetupJinnyAccountActivity?.toast(it)
                    showDialog(it)
                    Log.d("DEBUG", "FAILED")
                }
            }
        })
    }

    private fun hideBarCodeDialog() {
        SharePreference.getInstance().turnGuestToFull(tetEmailGuest?.text.toString())
        this.saveUserPref(SharePreference.getInstance().getGuestAccount() ?: return)
        JinnyApplication.instance.reloadCurUser()
        resultDialog?.cancel()
        startActivity(intentFor<MainActivity>().clearTask().newTask())
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = this@SetupJinnyAccountActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)
        return false
    }

    private fun getSignUpGuestModel(): SetupJinnyAccountViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@SetupJinnyAccountActivity)
                            .getAuthRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SetupJinnyAccountViewModel(repo) as T
                }

            })[SetupJinnyAccountViewModel::class.java]

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
}