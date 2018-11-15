package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Trace
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.fragment_promotion.*
import kotlinx.android.synthetic.main.layout_toolbar_mycashback.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.features.addvoucher.AddVoucherActivity
import sg.prelens.jinny.features.cashbackinfo.CashBackInfoActivity
import sg.prelens.jinny.models.HowToInfo
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.*
import sg.vinova.trackingtool.model.EventType

class PromotionFragment : Fragment(), VoucherListener, NestedScrollChangeListener {
    private var isChangeToArchived: Boolean = false
    private lateinit var fadeIn: ScaleAnimation
    private lateinit var fadeOut: ScaleAnimation

    companion object {
        fun newInstance() = PromotionFragment()
    }

    private var roomDB: RoomDB? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_promotion, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager()
        setUpFabAnimation()

        roomDB = RoomDB.newInstance(this@PromotionFragment.requireContext())

        if (activity?.getCurrentUser()?.isFirstTime_CashBack == true || SharePreference.getInstance().getGuestAccount()?.isFirstTime_CashBack == true) {
            startActivity(Intent(activity, CashBackInfoActivity::class.java)
                    .putExtra("STATUS", HowToInfo.CashBack)
                    .putExtra("isFIRSTTIME", true))
        }

        AppEvent.addVoucherListener(this)
        AppEvent.addNestedScrollListener(this)
        fab.setOnClickListener {
            if (it?.visibility == View.VISIBLE)
                startActivity(activity?.intentFor<AddVoucherActivity>()?.newTask())
        }

        ivInfo.setOnClickListener {
            startActivity(this@PromotionFragment.requireActivity().intentFor<CashBackInfoActivity>()
                    .putExtra("STATUS", HowToInfo.CashBack))
        }

    }

    private fun setupViewPager() {
        Trace.beginSection(this.tag + "setupViewPager")
        vpPromotions.adapter = PromotionPagerAdapter(fm = childFragmentManager)
        vpPromotions.offscreenPageLimit = 1
        tlPromotions.setupWithViewPager(vpPromotions)
        tlPromotions.setTabTextColors(resources.getColor(R.color.signInToggleTint), Color.RED)
        tlPromotions.setSelectedTabIndicatorColor(Color.RED)
        vpPromotions?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        if (!TrackingHelper.hasConnection(this@PromotionFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_discover, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@PromotionFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_discover, "", requireContext())?.observe(this@PromotionFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_discover, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@PromotionFragment.requireContext())
                            }
                        })
                        PromotionPageAllFragment.newInstance()
                    }
                    1 -> {
                        if (!TrackingHelper.hasConnection(this@PromotionFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_starred, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@PromotionFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_starred, "", requireContext())?.observe(this@PromotionFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_starred, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@PromotionFragment.requireContext())
                            }
                        })
                        PromotionPageStarredFragment.newInstance()

                    }
                    2 -> {
                        if (!TrackingHelper.hasConnection(this@PromotionFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_redeemed, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@PromotionFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_redeemed, "", requireContext())?.observe(this@PromotionFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_redeemed, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@PromotionFragment.requireContext())
                            }
                        })
                        PromotionPageRedeemedFragment.newInstance()
                    }
                    3 -> {
                        if (!TrackingHelper.hasConnection(this@PromotionFragment.requireContext())) {
                            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_archived, description = null)
                            TrackingHelper.setDB(default, roomDB
                                    ?: return, this@PromotionFragment.requireContext())
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.deals_archived, "", requireContext())?.observe(this@PromotionFragment, Observer {
                            if (it?.state == State.FAILED) {
                                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.deals_archived, description = null)
                                TrackingHelper.setDB(default, roomDB
                                        ?: return@Observer, this@PromotionFragment.requireContext())
                            }
                        })
                        PromotionPageArchivedFragment.newInstance()
                    }
                }
            }

        })
        Trace.endSection()
    }

    override fun onRefreshVoucher() {

    }

    fun setUpFabAnimation() {
        fadeIn = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        fadeOut = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)

        fadeIn.duration = 300
        fadeIn.fillAfter = true
        fadeOut.duration = 300
        fadeOut.fillAfter = true
    }

    override fun onResume() {
        super.onResume()
        if (isChangeToArchived) {
            vpPromotions.currentItem = 2
            isChangeToArchived = false
        }
    }

    override fun onRedeemVoucher() {
        isChangeToArchived = true
    }

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unregisterVoucherListener(this)
        AppEvent.unregisterNestedScrollListener(this)
    }

    override fun onScroll(isShow: Boolean) {
        Trace.beginSection(this.tag + "onScroll")
        if (!isShow && fab?.scaleX == 1f && fab?.visibility == View.VISIBLE) {
            fadeOut.hasEnded().run {
                fab?.startAnimation(fadeOut)
                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        fab?.visibility = View.GONE
                        fab?.x = fab?.x?.div(10) ?: 0f
                        fab?.y = fab?.y?.div(10) ?: 0f
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
        } else if (isShow && fab?.visibility == View.GONE) {
            fadeIn.hasEnded().run {
                fab?.x = fab?.x?.times(10) ?: 0f
                fab?.y = fab?.y?.times(10) ?: 0f
                fab?.startAnimation(fadeIn)
                fadeIn.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        fab?.visibility = View.VISIBLE
                    }

                    override fun onAnimationStart(animation: Animation?) {
                    }
                })
            }
        }

        if (!isShow) {
            Handler(Looper.getMainLooper()).postDelayed({
                fab?.visibility = View.GONE
            }, 300)
        } else fab?.visibility = View.VISIBLE

        Trace.endSection()
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//
//    }
}