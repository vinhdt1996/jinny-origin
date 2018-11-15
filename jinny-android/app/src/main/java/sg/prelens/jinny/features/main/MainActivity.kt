package sg.prelens.jinny.features.main

import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.*
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import kotlinx.android.synthetic.main.nav_right_menu.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseFragment
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.constant.MESSENGER_INTENT_KEY
import sg.prelens.jinny.constant.MSG_UPDATE_VOUCHER
import sg.prelens.jinny.exts.*
import sg.prelens.jinny.features.addvoucher.AddVoucherViewModel
import sg.prelens.jinny.features.auth.AuthActivity
import sg.prelens.jinny.features.auth.MainPagerAdapter
import sg.prelens.jinny.features.auth.setupjinnyaccount.SetupJinnyAccountActivity
import sg.prelens.jinny.features.cashbackdashboard.MyCashBackFragment
import sg.prelens.jinny.features.cashbackinfo.PrivacyActivity
import sg.prelens.jinny.features.cashbackinfo.TermsActivity
import sg.prelens.jinny.features.editprofile.EditProfileActivity
import sg.prelens.jinny.features.promotion.PromotionViewModel
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.features.membership.MembershipFragment
import sg.prelens.jinny.features.promotion.PromotionFragment
import sg.prelens.jinny.features.sendfeedback.SendFeedbackActivity
import sg.prelens.jinny.features.settings.SettingPrefs
import sg.prelens.jinny.features.settings.SettingViewModel
import sg.prelens.jinny.features.settings.SettingsActivity
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.service.IncomingMessageHandler
import sg.prelens.jinny.service.UpdateBadgeService
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.*
import sg.vinova.trackingtool.model.EventType
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), VoucherListener, MembershipListener, BadgeListener {
    private lateinit var logoutViewModel: LogoutViewModel
    private val addVoucherViewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@MainActivity).addShareDeal()
                return MainActivityViewModel(repo) as T
            }
        })[MainActivityViewModel::class.java]
    }
    var user: ProfileUser? = null
    private lateinit var logoutDialog: DialogInterface
    private val loadingProgress: Dialog by lazy {
        Dialog(this@MainActivity)
    }
    private lateinit var settingViewModel: SettingViewModel
    // Handler for incoming messages from the service.
    private lateinit var handler: IncomingMessageHandler
    private lateinit var serviceComponent: ComponentName
    private var currentTabPosition = 0
    private lateinit var prefSetting: SettingPrefs
    var membershipSearchView: EditText? = null
    var promotionSearchView: EditText? = null

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Trace.beginSection("MainActivity onCreate")
        setContentView(R.layout.activity_main)

        roomDB = RoomDB.newInstance(this)

        AppEvent.addUpdateBadgeListener(this)
        setupViewPager()
        settingViewModel = getSetting()
        val page = intent?.extras?.get("page") as? Int?
        if (page != null) {
            processViewPagerIntent(page)
        }

        getUser()
        bindEvent()
        handler = IncomingMessageHandler(this)
        prefSetting = SettingPrefs(this)
        scheduleJobUpdateBadge()
        openDialogPromtEnableNotification()
        Trace.endSection()

        val screen = intent?.extras?.get("screen") as? String?
        val id = intent?.extras?.get("id") as? String?

        if (screen != null) {
            processDeepLinkIntent(screen, id)
        } else {
            processDeepLinkIntent(SharePreference.getInstance().getScreenShareDeal()
                    ?: return, SharePreference.getInstance().getIdShareDeal())
        }

        val fromNotification = intent?.extras?.getBoolean("fromNotification") as? Boolean?
//        if(fromNotification == true)
//            FirebaseAnalyticsUtil.putNoParamAnalytics(this, "jinny_notification_open")


    }

    private fun processDeepLinkIntent(screen: String, id: String?) {
        when (screen) {
            "voucher" -> {
                addVoucherViewModel.requestShareDeal(id ?: return)
                // all api add voucher
                addVoucherViewModel.result.observe(this, Observer {
                    SharePreference.getInstance().saveShareDeal(null, null)
                    if (it != null) {
                        val deal = it
                        // if success start Activity
                        logoutDialog = alert {
                            customView {
                                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                                addView(view, null)
                                view.btnCancel?.visibility = View.GONE
                                view.btnOk?.setOnClickListener {
                                    hideDialog()
                                    startActivity<PromotionDetailActivity>("voucherId" to id,
                                            "usersVoucherId" to deal.result.users_voucher_id,
                                            "voucherName" to deal.result.merchant_name,
                                            "isArchived" to deal.result.archived,
                                            "isRedeemed" to deal.result.is_redeemed)
                                }
                                isCancelable = false
                                view.tvContent?.text = it.message
                            }
                        }.show()

                    }
                })
                // if fail show message
                addVoucherViewModel.error.observe(this, Observer {
                    SharePreference.getInstance().saveShareDeal(null, null)
                    if (it != null) {
                        logoutDialog = alert {
                            customView {
                                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                                addView(view, null)
                                view.btnCancel?.visibility = View.GONE
                                view.btnOk?.setOnClickListener {
                                    hideDialog()
                                }
                                isCancelable = false
                                view.tvContent?.text = it?.message
                            }
                        }.show()
                    }
                })

            }
        }

    }

    private fun processViewPagerIntent(page: Int) {
        viewPager.currentItem = page
    }

    private fun openDialogPromtEnableNotification() {
        if (!prefSetting.firstTimeOpenApp) {
            prefSetting.openSetting()
            //scheduleJobRemindVoucher()
            return
        } else {
            // prefSetting.openSetting()
            settingViewModel.settingResponse.observe(this, Observer {
                it?.let {
                    prefSetting.pushNotification = it.result.push_notification
                    prefSetting.storeDiscountAlert = it.result.store_discount_alert
                    prefSetting.voucherExpiryNotification = it.result.voucher_expiry
                    prefSetting.dayToRemindBeforeExpire = it.result.days_to_remind
                }
            })
            prefSetting.firstTimeOpenApp = false
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val page = intent?.extras?.get("page") as? Int?
        val isNotification = intent?.extras?.get("fromNotifcation") as? Boolean?
        if (isNotification == true) {
            TrackingHelper.sendEvent(EventType.APP_EVENT, AnalyticConst.notification_open, "", this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.notification_open, description = null)
                    TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                }
            })
        }
        processViewPagerIntent(page ?: return)
    }

    private fun getUser() {
        if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
            navigationView.getHeaderView(0).tvEditProfileAndSetupJinnyAccount?.text = getString(R.string.setup_jinny_account)
        } else {
            navigationView.getHeaderView(0).tvEditProfileAndSetupJinnyAccount?.text = getString(R.string.edit_profile)
        }
        user = getCurrentUser()
        user?.let {
            val isGuest = SharePreference.getInstance().getGuestAccount()?.guest ?: false
            navigationView.getHeaderView(0).tvAccount.text = getString(R.string.account)
            navigationView.getHeaderView(0).tvUser.text =
                    if (!isGuest) user?.email else "Guest"
        }
    }


    private fun setupViewPager() {
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = MainPagerAdapter(fragmentManager = supportFragmentManager, context = this)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setCustomView(R.layout.badge_tab_membership)
        tabLayout.getTabAt(1)?.setCustomView(R.layout.badge_tab_promotion)
        tabLayout.getTabAt(2)?.setCustomView(R.layout.badge_tab_cash_back)
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                AppEvent.notifyViewPagerChanged(position)
                currentTabPosition = position

                when (position) {
                    0 -> {
                        if (!TrackingHelper.hasConnection(this@MainActivity)) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_all, description = null)
                            TrackingHelper.setDB(default, roomDB ?: return, this@MainActivity)
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_all, "", this@MainActivity)?.observe(this@MainActivity, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_all, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MainActivity)
                            }
                        })
                        MembershipFragment.newInstance()
                    }
                    1 -> {
                        if (!TrackingHelper.hasConnection(this@MainActivity)) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_discover, description = null)
                            TrackingHelper.setDB(default, roomDB ?: return, this@MainActivity)
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_discover, "", this@MainActivity)?.observe(this@MainActivity, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_discover, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MainActivity)
                            }
                        })
                        PromotionFragment.newInstance()
                    }
                    2 -> {
                        if (!TrackingHelper.hasConnection(this@MainActivity)) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, description = null)
                            TrackingHelper.setDB(default, roomDB ?: return, this@MainActivity)
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, "", this@MainActivity)?.observe(this@MainActivity, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MainActivity)
                            }
                        })
                        MyCashBackFragment.newInstance()
                    }
                }
            }
        })
    }

    private fun bindEvent() {
        moreLayout.setOnClickListener {
            if (!drawer.isDrawerOpen(GravityCompat.END)) drawer.openDrawer(GravityCompat.END)
        }
        logoutViewModel = deleteAccountModel()
        navigationView.getHeaderView(0).tvLogout?.setOnClickListener {
            if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
                it?.run {
                    logoutDialog = alert {
                        customView {
                            val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                            addView(view, null)
                            view.tvContent.apply {
                                text = getString(R.string.msg_guest_exit)
                            }
                            view.btnOk.setText(R.string.yes)
                            view.btnCancel.setText(R.string.no)
                            view.btnOk.setOnClickListener {
                                loadingProgress.showLoading()
                                logoutViewModel.logoutAccountLiveData.observe(this@MainActivity, Observer {
                                    it?.let {
                                        logOutAsGuest()
                                    }
                                })
                                logoutViewModel.errorLiveData.observe(this@MainActivity, Observer {
                                    it?.parseMessage()?.let {
                                        showDialog(it, 0)
                                    }
                                })
                            }
                            view.btnCancel.setOnClickListener {
                                hideLogoutDialog()
                            }
                        }
                    }.show()
                }
            } else {
                loadingProgress.showLoading()
                logoutViewModel.logoutAccountLiveData.observe(this@MainActivity, Observer {
                    it?.let {
                        logOutAsUser()
                    }
                })
                logoutViewModel.errorLiveData.observe(this@MainActivity, Observer {
                    it?.parseMessage()?.let {
                        //                        toast(it)
//                        logOutAsUser()
                        showDialog(it, 0)
                    }
                })
            }
        }
        navigationView.getHeaderView(0).tvSetting?.setOnClickListener {
            drawer.closeDrawers()
            startActivity<SettingsActivity>()
        }
        navigationView.getHeaderView(0).tvEditProfileAndSetupJinnyAccount?.setOnClickListener {
            if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
                startActivity<SetupJinnyAccountActivity>()
            } else {
                startActivity<EditProfileActivity>()
            }
            drawer.closeDrawers()
        }
        navigationView.getHeaderView(0).tvFeedBack?.setOnClickListener {
            drawer.closeDrawers()
            startActivity<SendFeedbackActivity>()
        }
        navigationView.getHeaderView(0).tvTaC?.setOnClickListener {
            drawer.closeDrawers()
            startActivity<TermsActivity>()
        }
        navigationView.getHeaderView(0).tvPrivacy?.setOnClickListener {
            drawer.closeDrawers()
            startActivity<PrivacyActivity>()
        }
        navigationView.getHeaderView(0).tvShareApp?.setOnClickListener {
            drawer.closeDrawers()
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out this free deals & cashback app Jinny: " +
                    BuildConfig.BASE_URL + "share_app")
            startActivity(Intent.createChooser(sharingIntent, "Share using"))
        }
    }

    private fun hideLogoutDialog() {
        logoutDialog.cancel()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_DEL) {
            //toast(currentTabPosition.toString())
            val editTextSearch: EditText = when (currentTabPosition) {
                0 -> supportFragmentManager.fragments[0].view?.findViewById(R.id.edtSearch)
                        ?: membershipSearchView ?: return super.dispatchKeyEvent(event)
                1 -> promotionSearchView ?: return super.dispatchKeyEvent(event)
                else -> return super.dispatchKeyEvent(event)
            }
            val startSelection = editTextSearch.selectionStart
            val endSelection = editTextSearch.selectionEnd
            var text = editTextSearch.text?.toString() ?: ""
            if (editTextSearch.hasSelection()) {
                text = text.replaceRange(startSelection, endSelection, "")
                editTextSearch.setText(text)
                editTextSearch.setSelection(endSelection - (endSelection - startSelection))
            } else
                if (startSelection >= 1) {
                    text = text.replaceRange(startSelection - 1, endSelection, "")
                    editTextSearch.setText(text)
                    editTextSearch.setSelection(if (endSelection > 0) endSelection - 1 else 0)
                }
            return false
        }
        return super.dispatchKeyEvent(event)
    }

    fun logOutAsGuest() {
        thread(start = true) {
            FirebaseInstanceId.getInstance().deleteInstanceId()
        }

        val user_id = "user_id: " +  SharePreference.getInstance().getGuestAccount()?.id
        TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_signout, user_id, this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_signout, description = user_id)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        prefSetting.firstTimeOpenApp = true
        SharePreference.getInstance().deleteGuestAccount()
        deleteUserToken()
        hideLogoutDialog()
        loadingProgress.hideLoading()
        startActivity<AuthActivity>()
        this@MainActivity.finish()
    }

    fun logOutAsUser() {
        thread(start = true) {
            FirebaseInstanceId.getInstance().deleteInstanceId()
        }
        val user_id = "user_id: " + user?.id
        TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_signout, user_id, this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_signout, description = user_id)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        loadingProgress.hideLoading()
        prefSetting.resetSetting()
        prefSetting.firstTimeOpenApp = true
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
        this@MainActivity.deleteUserToken()
        (this@MainActivity.application as JinnyApplication).currentUser = null
        startActivity<AuthActivity>()
        this@MainActivity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterMembershipListener(this)
        AppEvent.unregisterVoucherListener(this)
        AppEvent.unregisterUpdateBadgeListener(this)
    }

    override fun onRestart() {
        super.onRestart()
        onReload()
    }

    private fun onReload() {
        val fragments = supportFragmentManager.fragments
        if (Util.isListValid(fragments)) {
            for (fragment in fragments) {
                if (fragment is BaseFragment) {
                    fragment.onReload()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopService(Intent(this, UpdateBadgeService::class.java))
    }

    override fun onStart() {
        super.onStart()
        try {
            val startServiceIntent = Intent(this, UpdateBadgeService::class.java)
            val messengerIncoming = Messenger(handler)
            startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
            startService(startServiceIntent)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Fabric.getLogger().e("MainActivity", e.getStackTraceString())
        }
    }

    private fun scheduleJobUpdateBadge() {
        serviceComponent = ComponentName(this, UpdateBadgeService::class.java)

        val builder = JobInfo.Builder(50, serviceComponent)

        builder.setMinimumLatency(2 * TimeUnit.SECONDS.toMillis(1))
        builder.setOverrideDeadline(2 * TimeUnit.SECONDS.toMillis(1))
        builder.setBackoffCriteria(2 * TimeUnit.SECONDS.toMillis(1),
                JobInfo.BACKOFF_POLICY_EXPONENTIAL)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        builder.run {
            setRequiresDeviceIdle(false)
            setRequiresCharging(false)
        }
        Log.d("MainActivity", "Scheduling job")
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(builder.build())
    }

    override fun onResume() {
        super.onResume()
        if (currentTabPosition != -1)
            viewPager.currentItem = currentTabPosition
    }

    override fun onRefreshMembership() {
        currentTabPosition = 0
    }

    override fun onRefreshVoucher() {
        currentTabPosition = 1
    }

    override fun onRedeemVoucher() {
        currentTabPosition = 1
    }

    override fun onUpdateBadge(size: Int) {

        val messengerIncoming = Messenger(handler)
        messengerIncoming.send(
                Message.obtain().apply {
                    what = MSG_UPDATE_VOUCHER
                    obj = size
                }
        )

    }

    fun showDialog(message: String?, flag: Int?) {
        logoutDialog = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialog()
                    } else {
                        hideDialog()
                        logOutAsGuest()
                    }
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    fun hideDialog() {
        logoutDialog.cancel()
    }

    private fun deleteAccountModel(): LogoutViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@MainActivity)
                            .deleteAccount()
                    @Suppress("UNCHECKED_CAST")
                    return LogoutViewModel(repo) as T
                }
            })[LogoutViewModel::class.java]

    private fun getSetting(): SettingViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@MainActivity).getSettingRepository()
                    @Suppress("UNCHECKED_CAST")
                    return SettingViewModel(repo) as T
                }
            })[SettingViewModel::class.java]
}