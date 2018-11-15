package sg.prelens.jinny.features.redeemcashback

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_cash_vouchers.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.CashbackVoucher

class CashBackVoucherAdapter(private var context: Context, private var voucherList: List<CashbackVoucher>?, private val glide: RequestManager) : RecyclerView.Adapter<CashBackVoucherAdapter.RedeemCashBackViewHolder>() {

    private lateinit var iOnItemClickListener: IOnItemClickListener

    fun setOnItemClickListener(iOnItemClickListener: IOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemCashBackViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_cash_vouchers, parent, false)
        return RedeemCashBackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return voucherList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RedeemCashBackViewHolder, position: Int) {
        holder.bindView(voucherList?.get(position), glide)
        holder.idButton()?.setOnClickListener {
            iOnItemClickListener.onItemClickListener(position)
        }
    }

    class RedeemCashBackViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bindView(cashBackVoucher: CashbackVoucher?, glide: RequestManager) {
            itemView?.ivCashVoucher?.loadFromUrl(cashBackVoucher?.image?.url?.original, glide)
            itemView?.tvVoucherName?.text = cashBackVoucher?.description
            itemView?.tvVoucherExpiredDate?.text = "Valid Till: ${cashBackVoucher?.expires_at_in_words}"
            itemView?.btnPurchase?.text = "$${cashBackVoucher?.price}"
            itemView?.tvVoucherType?.text = "(${cashBackVoucher?.cashback_type?.toLowerCase()})"
        }

        fun idButton(): Button? {
            return itemView?.btnPurchase
        }
    }

    fun setList(voucherList: List<CashbackVoucher>?) {
        this.voucherList = voucherList
        notifyDataSetChanged()
    }

    fun getList(): List<CashbackVoucher>? {
        return this.voucherList
    }
}