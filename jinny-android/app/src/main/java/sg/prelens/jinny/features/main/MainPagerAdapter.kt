package sg.prelens.jinny.features.auth

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.features.cashbackdashboard.MyCashBackFragment
import sg.prelens.jinny.features.membership.MembershipFragment
import sg.prelens.jinny.features.promotion.PromotionFragment

/**
 * Created by tommy on 3/10/18.
 */
class MainPagerAdapter(fragmentManager: FragmentManager, val context: Context) : FragmentStatePagerAdapter(fragmentManager) {
    companion object {
        const val NUM_TAB = 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MembershipFragment.newInstance()
            1 -> PromotionFragment.newInstance()
            2 -> MyCashBackFragment.newInstance()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }

    override fun getCount(): Int {
        return NUM_TAB
    }
}