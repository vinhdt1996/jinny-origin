package sg.prelens.jinny.features.promotion.adapter

import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.homepage.NetworkStateItemViewHolder
import sg.prelens.jinny.features.promotion.IOnItemPromotionClickListener
import sg.prelens.jinny.features.promotion.viewholder.PromotionViewHolder
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.repositories.NetworkState

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/19/18<br>.
 */
open class PromotionAdapter(private val glide: RequestManager
                       , private val retryCallback: () -> Unit
) : PagedListAdapter<PromotionList, RecyclerView.ViewHolder>(PROMOTION_COMPARATOR) {
    private var networkState: NetworkState? = null
    private lateinit var iOnItemPromotionClickListener: IOnItemPromotionClickListener

    fun setOnClickItemListener(iOnItemPromotionClickListener: IOnItemPromotionClickListener) {
        this.iOnItemPromotionClickListener = iOnItemPromotionClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_promotion -> {
                (holder as PromotionViewHolder).bind(getItem(position),iOnItemPromotionClickListener)
            }
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                    networkState)
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            payloads.forEach {
                (holder as PromotionViewHolder).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_promotion -> PromotionViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_promotion
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val PROMOTION_COMPARATOR = object : DiffUtil.ItemCallback<PromotionList>() {
            override fun areContentsTheSame(oldItem: PromotionList, newItem: PromotionList): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: PromotionList, newItem: PromotionList): Boolean =
                    oldItem.id == newItem.id

            override fun getChangePayload(oldItem: PromotionList, newItem: PromotionList): Any? {
                val diffBundle = Bundle()
                if (diffBundle.size() == 0) return null
                return diffBundle
            }
        }
    }

}