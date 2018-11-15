package sg.prelens.jinny.features.promotion.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.promotion.IOnItemPromotionClickListener
import sg.prelens.jinny.features.promotion.viewholder.PromotionViewHolder
import sg.prelens.jinny.models.PromotionList

class FeatureDealAdapter(val context: Context, var promotionList: List<PromotionList>?, val glide: RequestManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var iOnItemPromotionClickListener: IOnItemPromotionClickListener

    fun setOnClickItemListener(iOnItemPromotionClickListener: IOnItemPromotionClickListener) {
        this.iOnItemPromotionClickListener = iOnItemPromotionClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_promotion, parent, false)
        return PromotionViewHolder(view, glide)
    }

    override fun getItemCount(): Int = promotionList?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as PromotionViewHolder).bind(promotionList?.get(position), iOnItemPromotionClickListener)
    }

    fun setList(promotionList: List<PromotionList>?) {
        this.promotionList = promotionList
    }

    fun getList(): List<PromotionList>? = promotionList

}