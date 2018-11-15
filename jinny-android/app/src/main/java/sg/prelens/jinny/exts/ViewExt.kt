@file:Suppress("NOTHING_TO_INLINE")

package sg.prelens.jinny.exts

import android.support.annotation.ColorRes
import android.view.View

/**
 * Created by tommy on 3/22/18.
 */
inline fun View.setBackgroundColorResource(@ColorRes color:Int){
    this.setBackgroundColor(android.support.v4.content.ContextCompat.getColor(context,color))
}

inline fun View.gone(){
    visibility = View.GONE
}

inline fun View.visible(){
    visibility = View.VISIBLE
}