package sg.prelens.jinny.features.cashbackdashboard

import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_cashback_history.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.models.CashBackHistory


class CashBackDashboardViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): CashBackDashboardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cashback_history, parent, false)
            return CashBackDashboardViewHolder(view, glide)
        }
    }

    fun bind(cashBackHistory: CashBackHistory?, listenerImpl: OnItemClickCashBackListenerImpl) {
        view.apply {
            if (cashBackHistory?.bank_info == null && cashBackHistory?.amount == null) {
                tvHistoryName?.text = cashBackHistory?.voucher_description
                if (cashBackHistory?.cashback_amount.equals("0.00")) {
                    tvBonusAmount?.visibility = View.INVISIBLE
                    btnResubmit?.visibility = View.GONE
                } else {
                    btnResubmit?.visibility = View.GONE
                    tvBonusAmount?.visibility = View.VISIBLE
                    tvBonusAmount?.text = "+ $${cashBackHistory?.cashback_amount}"
                }
                tvStatus?.text = cashBackHistory?.cashback_status?.capitalize()
                clCashbackHistory?.isEnabled = true
            } else {
                if (!cashBackHistory.bank_info.isNullOrEmpty()) {
                    tvHistoryName?.text = cashBackHistory.bank_info
                    tvId?.visibility = View.GONE
                } else {
                    tvHistoryName?.text = cashBackHistory.voucher_description
                    tvId?.visibility = View.VISIBLE
                }
                tvBonusAmount?.visibility = View.VISIBLE
                tvBonusAmount?.text = "- $${cashBackHistory?.amount}"
                tvStatus?.text = cashBackHistory.status?.capitalize()
                clCashbackHistory?.isEnabled = false
            }
            when (if (!cashBackHistory?.cashback_status.isNullOrEmpty()) cashBackHistory?.cashback_status?.toLowerCase() else cashBackHistory?.status?.toLowerCase()) {
                "pending" -> {
                    btnResubmit?.visibility = View.GONE
                    tvStatus?.setTextColor(ContextCompat.getColor(context, R.color.pending))
                }
                "rejected" -> {
                    if (cashBackHistory?.allow_resubmit == true) {
                        btnResubmit?.visibility = View.VISIBLE // Cr6 stagging release
                    } else {
                        btnResubmit?.visibility = View.GONE
                    }
                    tvStatus?.setTextColor(ContextCompat.getColor(context, R.color.crimson))
                }
                else -> {
                    btnResubmit?.visibility = View.GONE
                    tvStatus?.setTextColor(ContextCompat.getColor(context, R.color.neon_green))
                }
            }
            //New logic which can be removed
            if ((!cashBackHistory?.description.isNullOrEmpty() || !cashBackHistory?.title.isNullOrEmpty()) && cashBackHistory?.isClicked == true) {
                tvStatus?.isEnabled = true
                tvStatus?.isSelected = true
                tvStatus?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_selector, 0)
                cvReason?.visibility = View.VISIBLE
            } else if ((!cashBackHistory?.description.isNullOrEmpty() || !cashBackHistory?.title.isNullOrEmpty()) && cashBackHistory?.isClicked == false) {
                tvStatus?.isEnabled = true
                tvStatus?.isSelected = false
                tvStatus?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_expand_selector, 0)
                cvReason?.visibility = View.GONE
            } else {
                tvStatus?.isEnabled = false
                tvStatus?.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                cvReason?.visibility = View.GONE
            }

            if (!cashBackHistory?.voucher_cashback_type.isNullOrEmpty()) {
                tvTypeVoucher?.visibility = View.VISIBLE
                tvTypeVoucher?.text = cashBackHistory?.voucher_cashback_type
            } else {
                tvTypeVoucher?.visibility = View.GONE
            }

            tvTime?.text = cashBackHistory?.updated_at
            tvReasonTitle?.text = cashBackHistory?.title
            if (cashBackHistory?.id?.contains("TXN") == true) {
                tvId?.text = cashBackHistory.id
            } else {
                tvId?.visibility = View.GONE
            }
            tvReasonDescription?.text = cashBackHistory?.description
            tvStatus?.setOnClickListener {
                listenerImpl.onItemClickListener(adapterPosition)
                tvStatus?.isSelected = !tvStatus?.isSelected!!
                cvReason?.startAnimation(AnimationUtils.loadAnimation(context,
                        if (tvStatus.isSelected) {
                            cvReason?.visibility = View.VISIBLE
                            R.anim.slide_down
                        } else {
                            Handler(Looper.getMainLooper()).postDelayed({
                                cvReason?.visibility = View.GONE
                            }, 250)
                            R.anim.slide_up
                        }))
            }
            clCashbackHistory?.setOnClickListener {
                listenerImpl.onItemClickCashBackListener(adapterPosition)
            }
            btnResubmit?.setOnClickListener{
                listenerImpl.onItemClickCashBackResubmitListener(adapterPosition)
            }
        }
    }

    fun update(item: Any?) {
//        val update = item as? Bundle
//        val image = update?.get("image") as String
//        if (!image.isEmpty()) {
//        }
    }
}