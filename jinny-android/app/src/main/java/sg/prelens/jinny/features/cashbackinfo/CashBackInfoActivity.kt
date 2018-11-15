package sg.prelens.jinny.features.cashbackinfo

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_cash_back_info.*
import kotlinx.android.synthetic.main.layout_toolbar_detail_normal.*
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.db.DataCashBackFactory
import sg.prelens.jinny.db.DataMembershipFactory
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.exts.saveFirstTimeCashBack
import sg.prelens.jinny.exts.saveFirstTimeMembership
import sg.prelens.jinny.models.HowToInfo
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.EventType

class CashBackInfoActivity : AppCompatActivity() {

    private var howToInfo: HowToInfo? = null
    private var isFirstTime: Boolean = false

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_back_info)

        roomDB = RoomDB.newInstance(this)

        howToInfo = intent?.extras?.getSerializable("STATUS") as HowToInfo?
        isFirstTime = intent?.extras?.getBoolean("isFIRSTTIME", false)!!

        if (!isFirstTime) {
            ivBack?.visibility = View.VISIBLE
            tvTitle?.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        } else {
            ivBack?.visibility = View.GONE
            tvTitle?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }


        ivBack.setOnClickListener {
            onBackPressed()
        }
        setupViewPager()

    }

    private fun setupViewPager() {
        when (howToInfo) {
            HowToInfo.CashBack -> {
                tvTitle.text = R.string.cashback_info.parseResString()
                if (!TrackingHelper.hasConnection(this)) {
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_how_to, description = null)
                    TrackingHelper.setDB(default, roomDB ?: return, this)
                }
                TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.cashbacks_how_to, "", this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.cashbacks_how_to, description = null)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                vpCashBackInfo.adapter = CashBackInfoPagerAdapter(fragmentManager = supportFragmentManager
                        , pages = DataCashBackFactory.getCashBackInfos(), isFirstTime = isFirstTime, status = howToInfo
                        ?: return)
            }
            else -> {
                tvTitle.text = R.string.add_membership_info.parseResString()
                if (!TrackingHelper.hasConnection(this)) {
                    val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_how_to, description = null)
                    TrackingHelper.setDB(default, roomDB ?: return, this)
                }
                TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.memberships_how_to, "", this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.memberships_how_to, description = null)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                vpCashBackInfo.adapter = CashBackInfoPagerAdapter(fragmentManager = supportFragmentManager
                        , pages = DataMembershipFactory.getCashBackInfos(), isFirstTime = isFirstTime, status = howToInfo
                        ?: return)
            }
        }

        tabLayout.setupWithViewPager(vpCashBackInfo, true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (howToInfo) {
            HowToInfo.CashBack -> {
                saveFirstTimeCashBack(false)
                SharePreference.getInstance().saveFirstTimeCashBack(false)
            }
            else -> {
                saveFirstTimeMembership(false)
                SharePreference.getInstance().saveFirstTimeMembership(false)
            }
        }
    }
}
