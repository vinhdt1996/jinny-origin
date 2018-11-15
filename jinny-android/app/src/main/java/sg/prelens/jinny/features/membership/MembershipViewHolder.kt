package sg.prelens.jinny.features.membership

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_membership.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.Membership

class MembershipViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {

    fun bind(membership: Membership?) {
        view.apply {
            ivMemberships.loadFromUrl(membership?.merchant?.logo?.url?.original, glide)
            when(membership?.has_bookmark){
                true -> ivNew.visibility = View.VISIBLE
                false -> ivNew.visibility = View.GONE
            }
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val imageUrl = update?.get("image_url") as? String
        if (!imageUrl.isNullOrEmpty()) {
            view.ivMemberships.loadFromUrl(imageUrl, glide)
        }
    }

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): MembershipViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_membership, parent, false)
            return MembershipViewHolder(view, glide)
        }
    }
}