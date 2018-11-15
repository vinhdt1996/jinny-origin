package sg.prelens.jinny.exts

import android.util.Patterns
import sg.prelens.jinny.JinnyApplication

/**
 * Created by tommy on 3/10/18.
 */
fun Int.parseResString(): String {
    return JinnyApplication.instance.getString(this)
}

fun String?.isValidEmail(): Boolean = !this.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String?.isValidPassword(): Boolean = !this.isNullOrEmpty()