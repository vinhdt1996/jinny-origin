@file:Suppress("NOTHING_TO_INLINE")

package sg.prelens.jinny.exts

import android.support.v4.app.Fragment
import org.jetbrains.anko.bundleOf

/**
 * Created by alan on 3/15/18.
 */
inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}