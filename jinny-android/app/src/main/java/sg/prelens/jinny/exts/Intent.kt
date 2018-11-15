package sg.prelens.jinny.exts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/14/18<br>.
 */
//can use anko
inline fun <reified T : Activity> Fragment.launchActivity(
        requestCode: Int = -1,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(context!!)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

//can use anko
inline fun <reified T : Activity> Context.launchActivity(
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

//can use anko
inline fun <reified T : Any> newIntent(context: Context): Intent =
        Intent(context, T::class.java)