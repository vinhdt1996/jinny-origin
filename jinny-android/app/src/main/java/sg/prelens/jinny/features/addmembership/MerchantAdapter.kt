package sg.prelens.jinny.features.addmembership

import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.homepage.NetworkStateItemViewHolder
import sg.prelens.jinny.models.Merchant
import sg.prelens.jinny.repositories.NetworkState

class MerchantAdapter(
        private val glide: RequestManager,
        private val retryCallback: () -> Unit)
    : PagedListAdapter<Merchant, RecyclerView.ViewHolder>(MERCHANT_COMPARATOR) {
    private var networkState: NetworkState? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_merchant -> (holder as MerchantViewHolder).bind(getItem(position), position == itemCount - 1)
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
                (holder as MerchantViewHolder).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_merchant -> MerchantViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_merchant
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
        val MERCHANT_COMPARATOR = object : DiffUtil.ItemCallback<Merchant>() {
            override fun areContentsTheSame(oldItem: Merchant, newItem: Merchant): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: Merchant, newItem: Merchant): Boolean =
                    oldItem.id == newItem.id

            override fun getChangePayload(oldItem: Merchant, newItem: Merchant): Any? {
                val diffBundle = Bundle()
                if (diffBundle.size() == 0) return null
                return diffBundle
            }
        }
    }
}