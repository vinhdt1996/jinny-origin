package sg.prelens.jinny.features.voucherbranch

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter


class VoucherBranchPagerAdapter(fm: FragmentManager, private val imageUrls: List<String>?) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return VoucherBranchPageFragment.newInstance(imageUrls?.get(position) ?: "", position)
    }

    override fun getCount(): Int {
        return imageUrls?.size ?: 0
    }
}