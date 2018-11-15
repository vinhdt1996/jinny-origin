package sg.prelens.jinny.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.OnLongClickListener
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.log

typealias OnItemClickListener = (parent: RecyclerView, view: View, position: Int, id: Long) -> Unit
typealias OnItemLongClickListener = (parent: RecyclerView, view: View, position: Int, id: Long) -> Boolean

/**
 * Created by tommy on 3/16/18.
 */
class ItemClickSupport private constructor(private val recyclerView: RecyclerView) {
    private var itemClickListener: OnItemClickListener? = null
    private var itemLongClickListener: OnItemLongClickListener? = null

    private val clickListener = View.OnClickListener { v ->
        if (itemClickListener != null) {
            val position = recyclerView.getChildAdapterPosition(v)
            val id = recyclerView.getChildItemId(v)
            itemClickListener?.invoke(recyclerView, v, position, id)
        }
    }

    private val longClickListener = OnLongClickListener { v ->
        if (itemLongClickListener != null) {
            val position = recyclerView.getChildAdapterPosition(v)
            val id = recyclerView.getChildItemId(v)
            return@OnLongClickListener itemLongClickListener?.invoke(recyclerView, v, position, id)
                    ?: false
        }
        false
    }
    private val attachListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (itemClickListener != null) {
                view.setOnClickListener(clickListener)
            }

            if (itemLongClickListener != null) {
                view.setOnLongClickListener(longClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            if (itemClickListener != null) {
                view.setOnClickListener(null)
            }

            if (itemLongClickListener != null) {
                view.setOnLongClickListener(null)
            }
        }
    }

    private fun attach(view: RecyclerView) {
        view.setTag(R.id.item_click_support, this)
        view.addOnChildAttachStateChangeListener(attachListener)
    }

    private fun detach(view: RecyclerView) {
        view.removeOnChildAttachStateChangeListener(attachListener)
        view.setTag(R.id.item_click_support, null)
        itemClickListener = null
        itemLongClickListener = null
    }

    /**
     * @return The callback to be invoked when an item in the [RecyclerView]
     * has been clicked, or `null` if no callback has been set.
     */
    fun getOnItemClickListener(): OnItemClickListener? = itemClickListener


    /**
     * Register a callback to be invoked when an item in the
     * [RecyclerView] has been clicked.
     *
     * @param listener callback that will be invoked
     */
    fun setOnItemClickListener(listener: OnItemClickListener): ItemClickSupport {
        itemClickListener = listener
        return this
    }

    /**
     * @return The callback to be invoked when an item in the [RecyclerView]
     * has been clicked and held, or `null` if no callback has been set.
     */
    fun getOnItemLongClickListener(): OnItemLongClickListener? {
        return itemLongClickListener
    }

    /**
     * Register a callback to be invoked when an item in the
     * [RecyclerView] has been clicked and held.
     *
     * @param listener callback that will be invoked
     */
    fun setOnItemLongClickListener(listener: OnItemLongClickListener): ItemClickSupport {
        itemLongClickListener = listener
        return this
    }

    /* */
    /**
     * Interface definition for a callback to be invoked when an item in the
     * [RecyclerView] has been clicked.
     *//*
    interface OnItemClickListener {
        */
    /**
     * Callback method to be invoked when an item in the `RecyclerView`
     * has been clicked.
     *
     * @param parent   `RecyclerView` where the click happened
     * @param view     view within the `RecyclerView` that was clicked
     * @param position position of the view in the adapter
     * @param id       row ID of the item that was clicked
     *//*
        fun onItemClick(parent: RecyclerView, view: View, position: Int, id: Long)
    }

    */
    /**
     * Interface definition for a callback to be invoked when an item in the
     * [RecyclerView] has been clicked and held.
     *//*
    interface OnItemLongClickListener {
        */
    /**
     * Callback method to be invoked when an item in the `RecyclerView`
     * has been clicked and held.
     *
     * @param parent   `RecyclerView` where the click happened
     * @param view     view within the `RecyclerView` that was clicked
     * @param position position of the view in the adapter
     * @param id       row ID of the item that was clicked
     * @return `true` if the callback consumed the long click; `false` otherwise.
     *//*
        fun onItemLongClick(parent: RecyclerView, view: View, position: Int, id: Long): Boolean
    }*/

    companion object {

        fun addTo(view: RecyclerView): ItemClickSupport {
            var support: ItemClickSupport? = view.getTag(R.id.item_click_support) as? ItemClickSupport
            if (support == null) {
                support = ItemClickSupport(view)
                support.attach(view)
            } else {
                log("RecyclerView already has ItemClickSupport.")
            }
            return support
        }

        fun removeFrom(view: RecyclerView): ItemClickSupport? {
            val support = view.getTag(R.id.item_click_support) as? ItemClickSupport
            support?.detach(view)
            return support
        }
    }
}