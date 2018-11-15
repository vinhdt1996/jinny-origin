package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_promotion_page.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.features.promotion.adapter.PromotionArchivedAdapter
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.view.View
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil

class PromotionPageArchivedFragment : PromotionPageFragment() {
    override fun getViewModel(): PromotionViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@PromotionPageArchivedFragment.requireContext())
                            .getPromotionArchivedRepository()
                    val opRepo = ServiceLocator.instance(this@PromotionPageArchivedFragment.requireContext())
                            .getOperationRepository()
                    val opRepoFilter = ServiceLocator.instance(this@PromotionPageArchivedFragment.requireContext())
                            .filterPageListPromotion()
                    @Suppress("UNCHECKED_CAST")
                    return PromotionViewModel(repo, opRepo, opRepoFilter) as T
                }
            })[PromotionViewModel::class.java]

    override fun openPromotionDetail(position: Int) {
        activity?.startActivity<PromotionDetailActivity>(
                "voucherId" to voucherAdapter.currentList?.get(position)?.id,
                "usersVoucherId" to voucherAdapter.currentList?.get(position)?.users_voucher_id,
                "voucherName" to voucherAdapter.currentList?.get(position)?.merchant?.name,
                "isArchived" to true)
    }


    companion object {
        fun newInstance() = PromotionPageArchivedFragment()
    }

    override fun initList() {
        super.initList()
        tvVoucherDescription?.text = getString(R.string.message_archived)
        val span = SpannableString("  " + tvVoucherDescription?.text)
        span.setSpan(ImageSpan(this.requireContext(), R.drawable.archive, DynamicDrawableSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvVoucherDescription?.text = span
        tvVoucher?.text = String.format(getString(R.string.title_voucher), "All")
        clFeatureDeal?.visibility = View.GONE
        rvOtherVoucher?.visibility = View.GONE
        rvVouchers?.visibility = View.VISIBLE
    }

    override fun initAdapter(glide: RequestManager) {
        voucherAdapter = PromotionArchivedAdapter(glide) {
            model.filter()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            rvVouchers?.visibility = View.VISIBLE
            rvOtherVoucher?.visibility = View.GONE
        }
    }
}