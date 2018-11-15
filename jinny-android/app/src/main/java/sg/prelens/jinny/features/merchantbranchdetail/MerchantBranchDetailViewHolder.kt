package sg.prelens.jinny.features.merchantbranchdetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_merchant_branch_detail.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.models.WorkingHours

class MerchantBranchDetailViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): MerchantBranchDetailViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_merchant_branch_detail, parent, false)
            return MerchantBranchDetailViewHolder(view, glide)
        }
    }

    fun bind(workingHours: WorkingHours?) {
        view.apply {
            tvOpeningDay.text = workingHours?.day
            tvOpeningHour.text = String.format(context.getString(R.string.working_time), workingHours?.start_time, workingHours?.close_time)
        }
    }
}