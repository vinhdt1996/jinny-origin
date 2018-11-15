package sg.prelens.jinny.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import java.lang.ref.WeakReference

/**
 * Created by Ray on 10/11/17.
 */

abstract class BaseDialog<T : BaseFragment, DATA>(t: T) {
    var dialog: Dialog? = null
    var weakReference: WeakReference<T>? = null
    var t: T? = t
    open var list: ArrayList<DATA> = ArrayList()

    abstract fun getLayoutId(): Int

    abstract fun init()

    abstract fun isTransparent(): Boolean

    abstract fun isTouchOutsideToCancel(): Boolean

    init {
        weakReference = WeakReference(t)
    }

    open fun setData(data: ArrayList<DATA>) {
        list = data
    }

    open fun show() {
        dialog = Dialog(weakReference?.get()?.context)
        val view: View = LayoutInflater.from(weakReference?.get()?.context).inflate(getLayoutId(), null)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(view)
        dialog?.setCanceledOnTouchOutside(!isTouchOutsideToCancel())
        if (isTransparent()) dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        init()
        dialog?.show()
    }

    open fun dismiss() {
        dialog?.dismiss()
    }
}
