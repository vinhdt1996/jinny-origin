package sg.prelens.jinny.base

import android.support.v7.widget.RecyclerView
import java.util.*

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/13/18<br>.
 */
abstract class BaseAdapter<DATA, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    internal var list: ArrayList<DATA> = ArrayList()
    open var lastPosition: Int = -1
    open var userId: Int = -1
    override fun onBindViewHolder(holder: VH, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    open fun getList(): ArrayList<DATA> {
        return list
    }

    open fun add(data: DATA) {
        if (data == null)
            return
        list.add(data)
        notifyItemInserted(list.size)
    }

    open fun add(data: DATA, position: Int) {
        if (data == null)
            return
        if (position < 0)
            return
        list.add(position, data)
        notifyItemInserted(list.size)
    }

    open fun addAll(list: ArrayList<DATA>) {
        val pos: Int = itemCount
        this.list.addAll(list)
        notifyItemRangeChanged(pos, this.list.size)
    }

    open fun addNull(data: DATA) {
        list.add(data)
        notifyItemInserted(list.size)
    }

    open fun remove(data: DATA) {
        list.remove(data)
        notifyDataSetChanged()
        notifyItemRangeChanged(0, list.size - 1)
    }

    open fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(0, list.size - 1)
        notifyDataSetChanged()
    }

    open fun clear() {
        val end: Int = this.list.size - 1
        list.clear()
        notifyItemRangeRemoved(0, end)
        notifyDataSetChanged()
    }

    open fun setList(data: ArrayList<DATA>) {
        list = data
        notifyDataSetChanged()
    }

    open fun getItemAt(position: Int): DATA? {
        val d: DATA? = null
        if (position in 0..(itemCount - 1)) {
            return list[position]
        }
        return d
    }

    open fun getCurrentMillis(): Long {
        return Date().time
    }

    open fun replaceItemAt(position: Int, data: DATA) {
        if (position < 0 || data == null) return
        list[position] = data
        notifyItemChanged(position)
    }
}