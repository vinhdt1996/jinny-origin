package sg.prelens.jinny.features.membershipdetail

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_promotion.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadCropTopFromUrl
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.Voucher

class RelatedPromotionViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): RelatedPromotionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion_line, parent, false)
            return RelatedPromotionViewHolder(view, glide)
        }
    }

    fun bind(voucher: Voucher?) {
        view.apply {
            ivMerchant.loadCropTopFromUrl(voucher?.image?.url?.original, glide)
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val image = update?.get("image") as String
        if (!image.isEmpty()) {
            view.ivMerchant.loadFromUrl(image, glide)
        }
    }
}