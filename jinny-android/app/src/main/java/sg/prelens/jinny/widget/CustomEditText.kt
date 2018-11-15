package sg.prelens.jinny.widget

import android.content.Context
import android.view.KeyEvent
import android.widget.EditText

class CustomEditText(context: Context) : EditText(context){
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event)
    }
}