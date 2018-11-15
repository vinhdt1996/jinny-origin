package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.fragment_promotion_page.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.utils.AppEvent
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil


open class PromotionPageAllFragment : PromotionPageFragment() {
    override fun getViewModel(): PromotionViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@PromotionPageAllFragment.requireContext())
                            .getPromotionRepository()
                    val opRepo = ServiceLocator.instance(this@PromotionPageAllFragment.requireContext())
                            .getOperationRepository()
                    val opRepoFilter = ServiceLocator.instance(this@PromotionPageAllFragment.requireContext())
                            .filterPageListPromotion()
                    @Suppress("UNCHECKED_CAST")
                    return PromotionViewModel(repo, opRepo, opRepoFilter) as T
                }
            })[PromotionViewModel::class.java]

    companion object {
        fun newInstance() = PromotionPageAllFragment()
    }

    override fun initList() {
        super.initList()
        tvVoucherDescription?.setText(R.string.message_discover)
        tvVoucher?.text = String.format(getString(R.string.title_voucher), "Other")
        clFeatureDeal?.visibility = View.VISIBLE // Feature deal
        rvOtherVoucher?.visibility = View.VISIBLE
        rvVouchers?.visibility = View.GONE
    }

    override fun updateBadge(size: Int?) {
        if (size != null)
            AppEvent.notifyUpdateBadge(size)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            //Cr6 not release
            rvVouchers?.visibility = View.GONE
            rvOtherVoucher?.visibility = View.VISIBLE
        }
    }

}