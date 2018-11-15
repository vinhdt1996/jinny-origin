package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_promotion_page.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.withArguments
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil

class PromotionPageStarredFragment : PromotionPageFragment() {
    override fun getViewModel(): PromotionViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@PromotionPageStarredFragment.requireContext())
                            .getPromotionStarredRepository()
                    val opRepo = ServiceLocator.instance(this@PromotionPageStarredFragment.requireContext())
                            .getOperationRepository()
                    val opRepoFilter = ServiceLocator.instance(this@PromotionPageStarredFragment.requireContext())
                            .filterPageListPromotion()
                    @Suppress("UNCHECKED_CAST")
                    return PromotionViewModel(repo, opRepo, opRepoFilter) as T
                }
            })[PromotionViewModel::class.java]

    companion object {
        fun newInstance() = PromotionPageStarredFragment()
    }

    override fun initList() {
        super.initList()
        tvVoucherDescription?.setText(getString(R.string.message_starred))
        val span = SpannableString("  " + tvVoucherDescription?.text)
        span.setSpan(ImageSpan(this.requireContext(), R.drawable.star_action_off, DynamicDrawableSpan.ALIGN_BASELINE), 9, 10, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tvVoucherDescription?.setText(span)
        tvVoucher?.text = String.format(getString(R.string.title_voucher), "All")
        clFeatureDeal?.visibility = View.GONE
        rvOtherVoucher?.visibility = View.GONE
        rvVouchers?.visibility = View.VISIBLE
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            rvVouchers?.visibility = View.VISIBLE
            rvOtherVoucher?.visibility = View.GONE
        }
    }
}