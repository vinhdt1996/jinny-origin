package sg.prelens.jinny.exts

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import sg.prelens.jinny.R

fun Dialog.showLoading() {
    this.apply {
        //        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_loading)
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    this.show()
}

fun Dialog.hideLoading() {
    this.cancel()
}