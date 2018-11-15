package sg.prelens.jinny.features.promotion

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.parseResString

class PromotionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val NUM_TAB = 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PromotionPageAllFragment.newInstance()
            1 -> PromotionPageStarredFragment.newInstance()
            3 -> PromotionPageArchivedFragment.newInstance()
            2 -> PromotionPageRedeemedFragment.newInstance()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }

    override fun getCount(): Int {
        return NUM_TAB
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> R.string.discover.parseResString()
            1 -> R.string.starred.parseResString()
            3 -> R.string.archived.parseResString()
            2 -> R.string.redeemed.parseResString()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }
}