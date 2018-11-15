package sg.prelens.jinny.features.cashbackdashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.features.cashbackdashboard.cashbackhistory.CashBackActivityFragment
import sg.prelens.jinny.features.cashbackdashboard.voucherhistory.VoucherPurchasedFragment
import sg.prelens.jinny.features.cashbackdashboard.withdrawhistory.WithdrawalHistoryFragment


class MyCashbackPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    companion object {
        const val NUM_TAB = 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CashBackActivityFragment.newInstance()
            1 -> WithdrawalHistoryFragment.newInstance()
            2 -> VoucherPurchasedFragment.newInstance()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }

    override fun getCount(): Int {
        return NUM_TAB
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> R.string.cash_back.parseResString()
            1 -> R.string.with_drawal.parseResString()
            2 -> R.string.purchases.parseResString()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }
}