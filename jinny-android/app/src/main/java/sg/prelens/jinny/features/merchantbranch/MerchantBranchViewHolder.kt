package sg.prelens.jinny.features.merchantbranch

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_merchant_branch.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.models.MerchantBranch

class MerchantBranchViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): MerchantBranchViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_merchant_branch, parent, false)
            return MerchantBranchViewHolder(view, glide)
        }
    }

    fun bind(merchantBranch: MerchantBranch?) {
        view.apply {
            tvTitleBranch.text = merchantBranch?.name
        }
    }

    fun update(item: Any?) {
        val update = item as? Bundle
        val image = update?.get("image") as String
        if (!image.isEmpty()) {
        }
    }
}