package sg.prelens.jinny.features.auth

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_promotion.*
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.vinova.trackingtool.model.EventType


/**
 * Created by tommy on 3/10/18.
 */
class AuthActivity : AppCompatActivity(), View.OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val imm: InputMethodManager = this@AuthActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v?.windowToken, 0)
        return false
    }

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setupViewPager()
        roomDB = RoomDB.newInstance(this)
        tabLayout.setupWithViewPager(viewPager)
        tlPromotions?.setSelectedTabIndicatorColor(ContextCompat.getColor(this@AuthActivity, R.color.text_black))
        tlPromotions?.setTabTextColors(ContextCompat.getColor(this@AuthActivity, R.color.outer_space), ContextCompat.getColor(this@AuthActivity, R.color.text_black));
        wrapTabIndicatorToTitle(tabLayout, 50, 50)
    }

    private fun setupViewPager() {
        val adapter = AuthPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        if (!TrackingHelper.hasConnection(this@AuthActivity)) {
                            saveDB()
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_signup, "", this@AuthActivity)?.observe(this@AuthActivity, Observer {
                            if (it?.state == State.FAILED) {
                                saveDB()
                            }
                        })
                    }
                    1 -> {
                        if (!TrackingHelper.hasConnection(this@AuthActivity)) {
                            saveDB()
                        }
                        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_login, "", this@AuthActivity)?.observe(this@AuthActivity, Observer {
                            if (it?.state == State.FAILED) {
                                saveDB()
                            }
                        })
                    }
                }
            }

        })
        main_constraint?.setOnTouchListener(this)
    }

    fun saveDB() {
        val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_signup, description = null)
        TrackingHelper.setDB(default, roomDB ?: return, this)
    }

    private fun wrapTabIndicatorToTitle(tabLayout: TabLayout, externalMargin: Int, internalMargin: Int) {
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            val childCount = tabStrip.childCount
            for (i in 0 until childCount) {
                val tabView = tabStrip.getChildAt(i)
                tabView.minimumWidth = 0
                tabView.setPadding(0, tabView.paddingTop, 0, tabView.paddingBottom)
                if (tabView.layoutParams is ViewGroup.MarginLayoutParams) {
                    val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
                    when (i) {
                        0 -> // left
                            setMargin(layoutParams, externalMargin, internalMargin)
                        childCount - 1 -> // right
                            setMargin(layoutParams, internalMargin, externalMargin)
                        else -> // internal
                            setMargin(layoutParams, internalMargin, internalMargin)
                    }
                }
            }
            tabLayout.requestLayout()
        }
    }

    private fun setMargin(layoutParams: ViewGroup.MarginLayoutParams, start: Int, end: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.marginStart = start
            layoutParams.marginEnd = end
        } else {
            layoutParams.leftMargin = start
            layoutParams.rightMargin = end
        }
    }
}