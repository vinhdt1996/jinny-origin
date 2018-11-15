package sg.prelens.jinny.features.promotion.viewholder

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_promotion.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.loadCropTopFromUrl
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.features.promotion.IOnItemPromotionClickListener
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

open class PromotionViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): PromotionViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion, parent, false)
            return PromotionViewHolder(view, glide)
        }
    }

    fun bind(promotion: PromotionList?, listener: IOnItemPromotionClickListener?) {
        view.apply {
            ivMerchant.loadCropTopFromUrl(promotion?.image?.url?.original, glide)
            tvMerchantName.text = promotion?.merchant?.name
            tvMerchantDetail.text = String.format(context.getString(R.string.expiry_date), promotion?.expires_at_in_words)
            if (promotion != null) {
                ivNew.visibility = if (promotion.is_read) View.GONE else View.VISIBLE
            } else return@apply
            ivStartArchived.setOnClickListener {
                if (promotion.is_bookmarked){
                    TrackingHelper.sendEventUtil(EventType.ACTION, AnalyticConst.deal_unbookmark, context.getString(R.string.deal_info, promotion.merchant_name, promotion.id), context)
                }else {
                    TrackingHelper.sendEventUtil(EventType.ACTION, AnalyticConst.deal_bookmark, context.getString(R.string.deal_info, promotion.merchant_name, promotion.id), context)
                }
                listener?.onItemStarClickListener(adapterPosition, promotion.is_bookmarked, promotion.id, promotion.users_voucher_id)
            }
            ivArchived.setOnClickListener({
                if (promotion.is_archived){
                    TrackingHelper.sendEventUtil(EventType.ACTION, AnalyticConst.deal_unarchive, context.getString(R.string.deal_info, promotion.merchant_name, promotion.id), context)
                }else {
                    TrackingHelper.sendEventUtil(EventType.ACTION, AnalyticConst.deal_archive, context.getString(R.string.deal_info, promotion.merchant_name, promotion.id), context)
                }
                listener?.onItemAtchivedClickListener(adapterPosition, promotion.is_archived, promotion.id, promotion.users_voucher_id)
            })
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

            processView(promotion)
        }

    }

    open protected fun processView(promotion: PromotionList) {
        view.apply {
            if (promotion.is_bookmarked) {
                ivStartArchived.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_action_on))
            } else {
                ivStartArchived.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.star_action_off))
            }
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val image = update?.get("image") as String
        if (!image.isEmpty()) {
            view.ivMerchant.loadFromUrl(image, glide)
        }
        val is_bookmarked = update.get("is_bookmarked") as? Boolean
        if (is_bookmarked == true) {
            view.ivStartArchived.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.star_action_on))
        } else {
            view.ivStartArchived.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.star_action_off))
        }
    }
}