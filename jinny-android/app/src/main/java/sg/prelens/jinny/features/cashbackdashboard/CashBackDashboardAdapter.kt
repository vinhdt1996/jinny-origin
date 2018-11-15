package sg.prelens.jinny.features.cashbackdashboard

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.base.BaseAdapter
import sg.prelens.jinny.features.homepage.NetworkStateItemViewHolder
import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.repositories.NetworkState

class CashBackDashboardAdapter(private val glide: RequestManager, private val retryCallback: () -> Unit)
    : BaseAdapter<CashBackHistory, RecyclerView.ViewHolder>(){
    private var networkState: NetworkState? = null
    private lateinit var listenerImpl: OnItemClickCashBackListenerImpl

    fun setOnItemClickListener(listenerImpl: OnItemClickCashBackListenerImpl){
        this.listenerImpl = listenerImpl
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_cashback_history -> (holder as CashBackDashboardViewHolder).bind(getItemAt(position), listenerImpl)
            R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
                    networkState)
        }
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
//        if (payloads.isNotEmpty()) {
//            payloads.forEach {
//                (holder as CashBackDashboardViewHolder).update(it)
//            }
//        } else {
            onBindViewHolder(holder, position)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_cashback_history -> CashBackDashboardViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_item
        } else {
            R.layout.item_cashback_history
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

}
