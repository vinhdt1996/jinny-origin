package sg.prelens.jinny.exts

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

/**
 * Created by tommy on 3/14/18.
 */
fun Activity.hideKeyboard() {
    val inputMethodManager = this.getSystemService(
            Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
            this.currentFocus?.windowToken, 0)
}

/**
 * Auto hide keyboard when touch outside input fields
 *
 * @param activity activity context
 * @param view     root view to receive touch listener
 */
fun Activity.setUpHideSoftKeyboard(view: View) {
    //Set up touch listener for non-text box views to hide keyboard.
    if (view !is EditText && view !is Button) {
        view.setOnTouchListener { v, event ->
            hideSoftKeyboard()
            false
        }
    }

    if (view is ViewGroup) { //If a layout container, iterate over children and seed recursion.
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            setUpHideSoftKeyboard(innerView)
        }
    }
}

fun Activity.hideSoftKeyboard() {
    val im = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(this.window.decorView.windowToken, 0)
}

fun Activity.showSoftKeyboard() {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInputFromWindow(this.window.decorView.windowToken,
            InputMethodManager.SHOW_FORCED, 0)
}
