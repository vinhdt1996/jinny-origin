package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.view.View
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_promotion_page.*
import sg.prelens.jinny.api.ServiceLocator
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.features.promotion.adapter.PromotionRedeemedAdapter
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil

class PromotionPageRedeemedFragment : PromotionPageFragment() {
    override fun getViewModel(): PromotionViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@PromotionPageRedeemedFragment.requireContext())
                            .getRedeemVoucherRepository()
                    val opRepo = ServiceLocator.instance(this@PromotionPageRedeemedFragment.requireContext())
                            .getOperationRepository()
                    val opRepoFilter = ServiceLocator.instance(this@PromotionPageRedeemedFragment.requireContext())
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
                "isRedeemed" to true)
    }

    companion object {
        fun newInstance() = PromotionPageRedeemedFragment()
    }

    override fun initList() {
        super.initList()
        tvVoucherDescription?.setText(R.string.message_redeemed)
        tvFeature?.visibility = View.GONE
        tvVoucher?.text = String.format(getString(R.string.title_voucher), "All")
        rvFeatureVouchers?.visibility = View.GONE
        clFeatureDeal?.visibility = View.GONE
        rvOtherVoucher?.visibility = View.GONE
        rvVouchers?.visibility = View.VISIBLE
    }

    override fun initAdapter(glide: RequestManager){
        voucherAdapter = PromotionRedeemedAdapter(glide){
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