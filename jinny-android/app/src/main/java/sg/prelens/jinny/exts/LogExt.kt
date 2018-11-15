package sg.prelens.jinny.exts

import android.util.Log
import sg.prelens.jinny.BuildConfig

/**
 * Created by tommy on 3/13/18.
 */
inline fun <reified T> T.log(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d(T::class.java.simpleName, msg)
    }
}