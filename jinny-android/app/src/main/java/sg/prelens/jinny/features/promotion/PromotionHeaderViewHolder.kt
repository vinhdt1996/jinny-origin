package sg.prelens.jinny.features.promotion

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import kotlinx.android.synthetic.main.item_promotion_header.view.*
import kotlinx.android.synthetic.main.layout_search_view.view.*
import org.jetbrains.anko.find
import sg.prelens.jinny.R
import sg.prelens.jinny.exts.parseResString
import sg.prelens.jinny.features.membership.NoPaddingArrayAdater

class PromotionHeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view), TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        query = s.toString()
//        searchListener.query(query, order)
    }

    private var query: String = ""
    private var order: String = ""

    companion object {
        fun create(parent: ViewGroup): PromotionHeaderViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_promotion_header, parent, false)
            return PromotionHeaderViewHolder(view)
        }
    }

    fun bind() {
        view.apply {
            tvVoucher?.text = String.format(R.string.title_voucher.parseResString(), "All")

            val spinnerArrayAdapter = NoPaddingArrayAdater<String>(
                    view.context, R.layout.layout_sort_by_item_promotion, view.context.resources.getStringArray(R.array.sorting_item_array_deal)
            )
            spinnerArrayAdapter.setDropDownViewResource(R.layout.layout_sort_by_item_promotion)
            spSortByPromotion?.adapter = spinnerArrayAdapter

//            svVoucher?.setOnTouchListener { _, _ ->
//                svVoucher?.isFocusableInTouchMode = true
//                svVoucher?.find<`SearchView.SearchAutoComplete>(R.id.search_src_text)?.setTextColor(
//                        ContextCompat.getColor(view.context,
//                                R.color.black)
//                )
//                false
//            }
            spSortByPromotion?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    order = when (position) {
                        0 -> "desc"
                        else -> "asc"
                    }
                    //searchListener.query(query, order)
                }
            }
            edtSearch?.addTextChangedListener(this@PromotionHeaderViewHolder)
//            svVoucher?.setOnQueryTextListener(object : SearchView.OnQueryTextListener, android.widget.SearchView.OnQueryTextListener {
//                override fun onQueryTextChange(q: String): Boolean {
//                    query = q
//                    //searchListener.query(query, order)
//                    return false
//                }
//
//                override fun onQueryTextSubmit(q: String): Boolean {
//                    return false
//                }
//            })

        }

    }

    interface OnSearchHeader {
        fun query(query: String, order: String)
    }

}