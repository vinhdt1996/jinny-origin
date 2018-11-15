package sg.prelens.jinny.features.cashbackdashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.fragment_my_cash_back.*
import kotlinx.android.synthetic.main.layout_toolbar_mycashback.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.exts.saveFirstTimeCashBack
import sg.prelens.jinny.exts.saveUserPref
import sg.prelens.jinny.features.cashbackinfo.CashBackInfoActivity
import sg.prelens.jinny.features.redeemcashback.RedeemCashBackActivity
import sg.prelens.jinny.models.HowToInfo
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.CashBackOverViewListener
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.EventType

class MyCashBackFragment : Fragment(), CashBackOverViewListener {

    companion object {

        fun newInstance(): MyCashBackFragment {
            val fragment = MyCashBackFragment()
            return fragment
        }
    }

    private var roomDB: RoomDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_my_cash_back, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppEvent.addCashBackListener(this)
        setupViewPager()

        roomDB = RoomDB.newInstance(this@MyCashBackFragment.requireContext())

        btnRedeemCashBack.setOnClickListener { activity?.startActivity<RedeemCashBackActivity>() }
        ivInfo.setOnClickListener {
            startActivity(Intent(activity, CashBackInfoActivity::class.java)
                    .putExtra("STATUS", HowToInfo.CashBack))
        }
        init()
    }

    private fun setupViewPager() {
        viewPager.adapter = MyCashbackPagerAdapter(fm = childFragmentManager)
        viewPager.offscreenPageLimit = 2
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.setTabTextColors(resources.getColor(R.color.signInToggleTint), Color.RED)
        tabLayout.setSelectedTabIndicatorColor(Color.RED)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        if (!TrackingHelper.hasConnection(this@MyCashBackFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@MyCashBackFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, "", requireContext())?.observe(this@MyCashBackFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_activity, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MyCashBackFragment.requireContext())
                            }
                        })
                    }
                    1 -> {
                        if (!TrackingHelper.hasConnection(this@MyCashBackFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_withdrawal_history, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@MyCashBackFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_withdrawal_history, "", requireContext())?.observe(this@MyCashBackFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_withdrawal_history, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MyCashBackFragment.requireContext())
                            }
                        })
                    }
                    2 -> {
                        if (!TrackingHelper.hasConnection(this@MyCashBackFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_vouchers_purchased, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@MyCashBackFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_vouchers_purchased, "", requireContext())?.observe(this@MyCashBackFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_vouchers_purchased, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@MyCashBackFragment.requireContext())
                            }
                        })
                    }
                }
            }

        })
    }

    private fun init() {
        cashBackOverViewViewModel.cashBackOverViewLiveData.observe(this, Observer {
            tvDollarCashBackAvailable.text = "$${it?.body?.cashback_available ?: 0.00}"
            tvDollarAccumulated.text = "$${it?.body?.accumulated ?: 0.00}"
            tvDollarPendingVerification.text = "$${it?.body?.pending_verification ?: 0.00}"
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterCashBackListener(this)
    }

    override fun onStart() {
        super.onStart()
        cashBackOverViewViewModel.refresh()
    }

    override fun onRefreshCashBackOverView() {
        cashBackOverViewViewModel.refresh()
    }


//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
////        if (userVisibleHint) {
////            if (activity?.getCurrentUser()?.isFirstTime_CashBack == true || SharePreference.getInstance().getGuestAccount()?.isFirstTime_CashBack == true) {
////                startActivity(Intent(activity, CashBackInfoActivity::class.java)
////                        .putExtra("STATUS", HowToInfo.CashBack)
////                        .putExtra("isFIRSTTIME", true))
////            }
////        }
//    }

    private val cashBackOverViewViewModel: CashBackOverViewViewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(
                        this@MyCashBackFragment.requireContext())
                        .getCashBackOverView()
                return CashBackOverViewViewModel(repo) as T
            }
        })[CashBackOverViewViewModel::class.java]
    }
}