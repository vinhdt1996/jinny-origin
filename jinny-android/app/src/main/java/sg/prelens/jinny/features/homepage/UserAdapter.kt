package sg.prelens.jinny.features.homepage

import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.models.User
import sg.prelens.jinny.repositories.NetworkState


/**
 * Created by vinova on 1/18/18.
 */
class UserAdapter(
        private val glide: RequestManager,
        private val retryCallback: () -> Unit)
    : PagedListAdapter<User, RecyclerView.ViewHolder>(USER_COMPARATOR) {
    private var networkState: NetworkState? = null
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_user -> (holder as UserViewHolder).bind(getItem(position))
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
                (holder as UserViewHolder).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_user -> UserViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_user
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
        val USER_COMPARATOR = object : DiffCallback<User>() {
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                    oldItem.username == newItem.username

            override fun getChangePayload(oldItem: User, newItem: User): Any? {
                val diffBundle = Bundle()
                if (oldItem.avatar != newItem.avatar) {
                    diffBundle.putString("avatar", newItem.avatar)
                }
                if (diffBundle.size() == 0) return null
                return diffBundle
            }
        }
    }
}