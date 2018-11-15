package sg.prelens.jinny.features.merchantbranch

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import sg.prelens.jinny.R
import sg.prelens.jinny.features.merchantbranchdetail.MerchantBranchDetailViewHolder
import sg.prelens.jinny.models.WorkingHours

class MerchantBranchDetailAdapter(private val glide: RequestManager, private val workingTimes: List<WorkingHours?>)
    : RecyclerView.Adapter<MerchantBranchDetailViewHolder>() {
    override fun getItemCount(): Int {
        return workingTimes.size
    }

    override fun onBindViewHolder(holder: MerchantBranchDetailViewHolder, position: Int) {
        holder.bind(workingTimes[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_merchant_branch_detail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantBranchDetailViewHolder {
        return when (viewType) {
            R.layout.item_merchant_branch_detail -> MerchantBranchDetailViewHolder.create(parent, glide)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }
}