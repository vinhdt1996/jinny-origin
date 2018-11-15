package sg.prelens.jinny.features.promotiondetail

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.models.Logo

class PromotionDetailAdapter(private val glide: RequestManager, private var logos: List<Logo>)
    : RecyclerView.Adapter<PromotionDetailHolder>() {
    override fun getItemCount(): Int {
        return logos.size
    }

    override fun onBindViewHolder(holder: PromotionDetailHolder, position: Int) {
        holder.bind(logos[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_promotion_image
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionDetailHolder {
        return when (viewType) {
            R.layout.item_promotion_image -> PromotionDetailHolder.create(parent, glide)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    fun setList(listLogo: List<Logo>) {
        logos = listLogo
        notifyDataSetChanged()
    }


}