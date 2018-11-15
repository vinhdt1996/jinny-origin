package sg.prelens.jinny.exts

import android.support.v7.widget.RecyclerView
import sg.prelens.jinny.utils.ItemClickSupport
import sg.prelens.jinny.utils.OnItemClickListener
import sg.prelens.jinny.utils.OnItemLongClickListener

/**
 * Created by tommy on 3/16/18.
 */
fun RecyclerView.setOnItemClickListener(listener: OnItemClickListener) {
    ItemClickSupport.addTo(this).setOnItemClickListener(listener)
}

fun RecyclerView.setOnItemLongClickListener(listener: OnItemLongClickListener) {
    ItemClickSupport.addTo(this).setOnItemLongClickListener(listener)
}