package sg.prelens.jinny.widget.dialog

import android.content.DialogInterface
import android.view.View
import org.jetbrains.annotations.NotNull
import sg.prelens.jinny.R
import sg.prelens.jinny.base.BaseDialog
import sg.prelens.jinny.base.BaseFragment

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
class LoadingDialog<E : BaseFragment> : BaseDialog<E, Object>, DialogInterface.OnDismissListener {
    private var progressBar: RotateLoading? = null

    constructor(@NotNull baseFragment: E) : super(baseFragment)

    override fun getLayoutId(): Int {
        return R.layout.layout_loading
    }

    override fun init() {
        dialog?.setOnDismissListener(this)
        if (progressBar != null) {
            progressBar!!.visibility = View.VISIBLE
            progressBar!!.start()
        }
    }

    override fun isTransparent(): Boolean {
        return true
    }

    override fun isTouchOutsideToCancel(): Boolean {
        return true
    }

    override fun onDismiss(dialog: DialogInterface?) {
        if (progressBar != null) {
            progressBar!!.visibility = View.GONE
        }
    }
}