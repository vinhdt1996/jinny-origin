package sg.prelens.jinny.features.membership

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.base.BaseAdapter
import sg.prelens.jinny.models.Membership

class MembershipAdapter(private val glide: RequestManager)
    : BaseAdapter<Membership, RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MembershipViewHolder).bind(getItemAt(position))
    }

    override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            position: Int,
            payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            payloads.forEach {
                (holder as MembershipViewHolder).update(it)
            }
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembershipViewHolder.create(parent, glide)
    }


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_membership
    }

//    companion object {
//        val MEMBERSHIP_COMPARATOR = object : DiffUtil.ItemCallback<Membership>() {
//            override fun areContentsTheSame(oldItem: Membership, newItem: Membership): Boolean =
//                    oldItem == newItem
//
//            override fun areItemsTheSame(oldItem: Membership, newItem: Membership): Boolean =
//                    oldItem.id == newItem.id
//
//            override fun getChangePayload(oldItem: Membership, newItem: Membership): Any? {
//                val diffBundle = Bundle()
//                if (oldItem.merchant?.logo?.url?.original != newItem.merchant?.logo?.url?.original) {
//                    diffBundle.putString("image_url", newItem.merchant?.logo?.url?.original)
//                }
//                if (diffBundle.size() == 0) return null
//                return diffBundle
//            }
//        }
//    }
}