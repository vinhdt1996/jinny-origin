package sg.prelens.jinny.features.cashbackinfo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sg.prelens.jinny.models.CashBackInfoModel
import sg.prelens.jinny.models.HowToInfo

class CashBackInfoPagerAdapter(val fragmentManager: FragmentManager,val pages: List<CashBackInfoModel>, val isFirstTime : Boolean, val status: HowToInfo): FragmentStatePagerAdapter(fragmentManager){
    override fun getItem(position: Int): Fragment {
        when (isFirstTime){
            true -> {
                when (position){
                    pages.size -> {
                        return HowToFirstTimeInfo.newInstance(status)
                    }
                    else -> {
                        return CashBackInfoPageFragment.newInstance(pages[position])
                    }
                }
            }
            else -> {
                return CashBackInfoPageFragment.newInstance(pages[position])
            }
        }

    }

    override fun getCount(): Int {
        when (isFirstTime){
            true -> return pages.size + 1
            else -> return pages.size
        }
    }


}