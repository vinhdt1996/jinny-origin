package sg.prelens.jinny.features.settings

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.hideLoading
import sg.prelens.jinny.exts.showLoading
import sg.prelens.jinny.features.auth.change.ChangePasswordActivity
import sg.prelens.jinny.models.SettingNotificationModel
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType


class SettingsActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.activity_settings
    var prefs: SettingPrefs? = null

    private lateinit var settingViewModel: SettingViewModel
    private val loadingProgres: Dialog by lazy {
        Dialog(this@SettingsActivity)
    }

    private var roomDB: RoomDB? = null

    override fun init() {
        super.init()

        roomDB = RoomDB.newInstance(this)

        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_settings, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.app_settings, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_settings, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })
        setTitle(getString(R.string.settings), 0)
        setBackgroundToolbar(0)
        settingViewModel = getSetting()
        loadingProgres.showLoading()
        ivBack?.setOnClickListener(this)
        lnChangePass.setOnClickListener {
            startActivity<ChangePasswordActivity>()
        }

        prefs = SettingPrefs(this)
        settingViewModel.settingResponse.observe(this, Observer {
            it?.let {
                loadingProgres.hideLoading()
                tgPushNoti?.isChecked = it.result.push_notification
                prefs?.pushNotification = it.result.push_notification
                tgDiscountAlert?.isChecked = it.result.store_discount_alert
                prefs?.storeDiscountAlert = it.result.store_discount_alert
                tgVoucherNoti?.isChecked = it.result.voucher_expiry
                prefs?.voucherExpiryNotification = it.result.voucher_expiry
                spnDays?.setSelection(it.result.days_to_remind - 1)
                prefs?.dayToRemindBeforeExpire = it.result.days_to_remind
            }
        })

        settingViewModel.putSetting.observe(this, Observer {
            it?.let {
                loadingProgres.hideLoading()
                tgPushNoti?.isChecked = it.result.push_notification
                prefs?.pushNotification = it.result.push_notification
                tgDiscountAlert?.isChecked = it.result.store_discount_alert
                prefs?.storeDiscountAlert = it.result.store_discount_alert
                tgVoucherNoti?.isChecked = it.result.voucher_expiry
                prefs?.voucherExpiryNotification = it.result.voucher_expiry
                spnDays?.setSelection(it.result.days_to_remind - 1)
                prefs?.dayToRemindBeforeExpire = it.result.days_to_remind
            }
        })
        checkSetting()
        initSpinnerDays()
    }

    private fun togglePushNotiGroup(isEnable: Boolean) {
        tgVoucherNoti.isEnabled = isEnable
        tgDiscountAlert.isEnabled = isEnable

        if (!isEnable) {
            tgDiscountAlert.isChecked = false
            tgVoucherNoti.isChecked = false
        } else {
            tgDiscountAlert.isChecked = true
            tgVoucherNoti.isChecked = true
        }
        spnDays.isEnabled = (isEnable && tgVoucherNoti.isChecked)
        settingViewModel.setSetting(getSettingView())
    }

    private fun checkSetting() {

        val isPushNotification = prefs?.pushNotification
        tgPushNoti.isChecked = isPushNotification ?: false
        tgPushNoti.setOnCheckedChangeListener { _, isChecked ->
            loadingProgres.showLoading()
            prefs?.pushNotification = isChecked
//            tgDiscountAlert?.isChecked = isChecked
//            prefs?.storeDiscountAlert = isChecked
//            tgVoucherNoti?.isChecked = isChecked
//            prefs?.voucherExpiryNotification = isChecked
            togglePushNotiGroup(isChecked)
        }

        tgDiscountAlert.apply {
            isChecked = prefs?.storeDiscountAlert ?: false
            isEnabled = isPushNotification ?: true
            setOnCheckedChangeListener { _, isChecked ->
                if (tgVoucherNoti?.isChecked == false) {
                    tgPushNoti?.isChecked = isChecked
                    prefs?.pushNotification = isChecked
                    togglePushNotiGroup(isChecked)
                }
                loadingProgres.showLoading()
                prefs?.storeDiscountAlert = isChecked
                settingViewModel.setSetting(getSettingView())
            }
        }

        tgVoucherNoti.apply {
            isEnabled = isPushNotification ?: true
            isChecked = prefs?.voucherExpiryNotification ?: false
            setOnCheckedChangeListener { _, isChecked ->
                loadingProgres.showLoading()
                if (tgDiscountAlert?.isChecked == false) {
                    tgPushNoti?.isChecked = isChecked
                    prefs?.pushNotification = isChecked
                    togglePushNotiGroup(isChecked)
                }
                prefs?.voucherExpiryNotification = isChecked
                rlDayChooser.alpha = if (isChecked) 1f else 0.5f
                spnDays.isEnabled = (isChecked)
                settingViewModel.setSetting(getSettingView())
            }
        }

        rlDayChooser.alpha = if (prefs?.voucherExpiryNotification ?: true) 1f else 0.5f
        tvAppVersion.text = getString(R.string.version) + BuildConfig.VERSION_NAME
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private fun initSpinnerDays() {
        val listDays = arrayListOf<String>()
        listDays.addAll(resources.getStringArray(R.array.days))
        val adapterDays = ArrayAdapter<String>(this@SettingsActivity, android.R.layout.simple_spinner_dropdown_item, listDays)
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnDays.apply {
            adapter = adapterDays
            isEnabled = prefs?.pushNotification ?: true
            isEnabled = prefs?.voucherExpiryNotification ?: true
        }
        spnDays.setSelection((prefs?.dayToRemindBeforeExpire ?: 7) - 1)
        spnDays.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadingProgres.showLoading()
                prefs?.dayToRemindBeforeExpire = position + 1
                settingViewModel.setSetting(getSettingView())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (!(prefs?.pushNotification ?: false) || !(prefs?.voucherExpiryNotification ?: false)) {
//            // (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
//        } else {
//            if (prefs?.voucherExpiryNotification ?: false &&
//                    (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).allPendingJobs.size < 2) {
//                (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).apply {
//                    cancel(51)
//                    schedule(
//                            JobUtil.getJobBuilder(
//                                    context = this@SettingsActivity,
//                                    jobId = 51)
//                                    .build()
//                    )
//                }
//
//            }
//        }
    }

    override fun replaceFragmentId(): Int = 0

    override fun isFullScreen(): Boolean = false

    override fun isBackPressed(): Boolean = true

    private fun getSetting(): SettingViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@SettingsActivity).getSettingRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SettingViewModel(repo) as T
                }
            })[SettingViewModel::class.java]

    private fun getSettingView(): SettingNotificationModel {
        val settingNotificationModel = SettingNotificationModel(tgPushNoti.isChecked,
                tgDiscountAlert.isChecked, tgVoucherNoti.isChecked, spnDays.selectedItemPosition + 1)
        return settingNotificationModel
    }
}