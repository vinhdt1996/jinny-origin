package sg.prelens.jinny.features.auth

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.parseResString

/**
 * Created by tommy on 3/10/18.
 */
class AuthPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    companion object {
        const val NUM_TAB = 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SignUpFragment.newInstance()
            1 -> SignInFragment.newInstance()
            else -> throw IllegalArgumentException("Wrong position")
        }
    }

    override fun getCount(): Int {
        return NUM_TAB
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) R.string.sign_up.parseResString()
        else R.string.sign_in.parseResString()
    }

}