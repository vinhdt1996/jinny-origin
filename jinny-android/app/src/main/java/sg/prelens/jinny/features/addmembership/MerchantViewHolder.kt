package sg.prelens.jinny.features.addmembership

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_merchant.view.*
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.loadFromUrl
import sg.prelens.jinny.models.Merchant

/**
 * Created by vinova on 1/18/18.
 */
class MerchantViewHolder(private val view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {

    fun bind(merchant: Merchant?, isLast: Boolean) {
        view.apply {
            tvName.text = merchant?.name
            ivLogo.loadFromUrl(merchant?.logo?.url?.original,
                    glide)
//            vLine.visibility = if(isLast) View.GONE else View.VISIBLE
        }
    }

    fun update(item: Any?) {
        //TODO
    }

    companion object {
        fun create(parent: ViewGroup, glide: RequestManager): MerchantViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_merchant, parent, false)
            return MerchantViewHolder(view, glide)
        }
    }

}