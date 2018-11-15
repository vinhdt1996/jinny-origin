package sg.prelens.jinny.features.merchantbranch

import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.homepage.NetworkStateItemViewHolder
import sg.prelens.jinny.models.MerchantBranch
import sg.prelens.jinny.repositories.NetworkState

class MerchantBranchAdapter(private val glide: RequestManager, private val retryCallback: () -> Unit)
    : PagedListAdapter<MerchantBranch, RecyclerView.ViewHolder>(RELATED_PROMOTION_COMPARATOR) {
    private var networkState: NetworkState? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_merchant_branch -> (holder as MerchantBranchViewHolder).bind(getItem(position))
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
                (holder as MerchantBranchViewHolder).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_merchant_branch -> MerchantBranchViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_merchant_branch
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    companion object {
        val RELATED_PROMOTION_COMPARATOR = object : DiffUtil.ItemCallback<MerchantBranch>() {
            override fun areContentsTheSame(oldItem: MerchantBranch, newItem: MerchantBranch): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: MerchantBranch, newItem: MerchantBranch): Boolean =
                    oldItem.id == newItem.id

            override fun getChangePayload(oldItem: MerchantBranch, newItem: MerchantBranch): Any? {
                val diffBundle = Bundle()
                if (diffBundle.size() == 0) return null
                return diffBundle
            }
        }
    }
}