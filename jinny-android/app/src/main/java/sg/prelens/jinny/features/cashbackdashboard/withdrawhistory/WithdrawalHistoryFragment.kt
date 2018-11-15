package sg.prelens.jinny.features.cashbackdashboard.withdrawhistory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_cash_back_page.*
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.features.cashbackdashboard.CashBackDashboardViewModel
import sg.prelens.jinny.features.cashbackdashboard.MyCashBackPageFragment
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil

open class WithdrawalHistoryFragment : MyCashBackPageFragment() {
    companion object {
        fun newInstance() = WithdrawalHistoryFragment()
    }

    override fun getViewModel(): CashBackDashboardViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@WithdrawalHistoryFragment.requireContext())
                            .getWithdrawalHistory()
                    @Suppress("UNCHECKED_CAST")
                    return CashBackDashboardViewModel(repo) as T
                }
            })[CashBackDashboardViewModel::class.java]

    override fun init() {
        super.init()
        tvNoItem?.text = getString(R.string.message_withdraw_history)
    }
}