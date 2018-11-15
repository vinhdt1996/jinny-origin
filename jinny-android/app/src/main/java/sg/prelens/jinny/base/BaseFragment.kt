package sg.prelens.jinny.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.annotation.UiThread
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sg.prelens.jinny.utils.Util
import sg.prelens.jinny.widget.dialog.LoadingDialog

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
abstract class BaseFragment : Fragment() {
    val loading: LoadingDialog<BaseFragment> by lazy {
        LoadingDialog(this@BaseFragment)
    }

    @UiThread
    open fun showLoading() {
        loading?.show()
    }

    @UiThread
    open fun hideLoading() {
        loading?.dismiss()
    }

    open fun onReload() {
        var fragments = childFragmentManager.fragments
        if (Util.isListValid(fragments)) {
            for (fragment in fragments) {
                if (fragment is BaseFragment) {
                    fragment.onReload()
                }
            }
        }
    }
}