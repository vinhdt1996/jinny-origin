package sg.prelens.jinny.features.redeemcashback

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_bank_account.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.models.BankInformation

class BankAdapter(private var context: Context, private var bankInformationList: List<BankInformation>?) : RecyclerView.Adapter<BankAdapter.RedeemCashBackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemCashBackViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_bank_account, parent, false)
        return RedeemCashBackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bankInformationList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RedeemCashBackViewHolder, position: Int) {
        holder.bindView(bankInformationList?.get(position))
    }


    class RedeemCashBackViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        fun bindView(bankInformation: BankInformation?) {
            itemView.tvNameRedeem.text = bankInformation?.holder_name
            itemView.tvAccountDecription.text = "${bankInformation?.bank_name} - ${bankInformation?.account_number_last_4}"
        }
    }

    fun setList(bankInformationList: List<BankInformation>?) {
        this.bankInformationList = bankInformationList
        notifyDataSetChanged()
    }

    fun getList(): List<BankInformation>? {
        return bankInformationList
    }

}