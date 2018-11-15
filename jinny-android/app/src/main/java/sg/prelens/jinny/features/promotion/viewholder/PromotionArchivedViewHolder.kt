package sg.prelens.jinny.features.promotion.viewholder

import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_promotion.view.*
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.gone
import sg.prelens.jinny.models.PromotionList

class PromotionArchivedViewHolder(private val view: View, private val glide: RequestManager) : PromotionViewHolder(view, glide) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): PromotionArchivedViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
            return PromotionArchivedViewHolder(view, glide)
        }
    }

    override fun processView(promotion: PromotionList) {
        view.apply {
            ivStartArchived?.visibility = View.GONE
            ivNew?.visibility = View.GONE
            if (!promotion.is_expired) {
                ivArchived?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.unarchive))
            } else {
                ivArchived?.visibility = View.GONE
            }
            tvCashBackAvailable?.visibility = View.GONE
        }
    }
}