package sg.prelens.jinny.features.promotion.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.homepage.NetworkStateItemViewHolder
import sg.prelens.jinny.features.promotion.viewholder.PromotionArchivedViewHolder
import sg.prelens.jinny.features.promotion.viewholder.PromotionViewHolder

class PromotionArchivedAdapter(private val glide: RequestManager
                               , private val retryCallback: () -> Unit) : PromotionAdapter(glide,retryCallback){


    override  fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_promotion -> PromotionArchivedViewHolder.create(parent, glide)
            R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

}