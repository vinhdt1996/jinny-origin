package sg.prelens.jinny.features.promotion.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_promotion.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.gone
import sg.prelens.jinny.exts.visible
import sg.prelens.jinny.models.PromotionList

class PromotionRedeemedViewHolder(private val view: View, private val glide: RequestManager): PromotionViewHolder(view,glide){
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): PromotionRedeemedViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
            return PromotionRedeemedViewHolder(view, glide)
        }
    }

    override fun processView(promotion: PromotionList) {
        view.apply {
            ivStartArchived.gone()
            when(promotion.can_cashback){
                true -> {
                    if (promotion.is_cashbacked == true) {
                        tvCashBackAvailable?.visibility = View.GONE
                    } else {
                        tvCashBackAvailable?.visibility = View.VISIBLE
                        if (promotion.cashback_type?.equals("cashback_percent") == true){
                            tvCashBackAvailable?.text = "Submit Receipt for " + promotion?.cashback_percent + "% Cashback"
                        }else if (promotion.cashback_type?.equals("cashback_amount") == true){
                            tvCashBackAvailable?.text = "Submit Receipt for $" + promotion?.cashback_amount + " Cashback"
                        }
                    }
                }
                false -> {
                    tvCashBackAvailable?.visibility = View.GONE
                }
            }
        }
    }
}