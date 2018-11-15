package sg.prelens.jinny.features.promotiondetail

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_promotion_image.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.Logo

class PromotionDetailHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): PromotionDetailHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion_image, parent, false)
            return PromotionDetailHolder(view, glide)
        }
    }

    fun bind(logo: Logo?) {
        view.run {
            ivPromotion.loadFromUrl(logo?.url?.original, glide)
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val image = update?.get("image") as String
        if (!image.isEmpty()) {
            view.ivPromotion.loadFromUrl(image, glide)
        }
    }
}