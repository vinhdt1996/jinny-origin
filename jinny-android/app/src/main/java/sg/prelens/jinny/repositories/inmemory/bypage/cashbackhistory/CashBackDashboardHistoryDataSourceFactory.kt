package sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.features.cashbackdashboard.MyCashBackPageFragment
import sg.prelens.jinny.models.CashBackHistory
import java.util.concurrent.Executor

class CashBackDashboardHistoryDataSourceFactory(private val api: ApiLink, private val retryExecutor: Executor, private val type: Int) : DataSource.Factory<Int, CashBackHistory> {
    val sourceCbActivityLiveData = MutableLiveData<CashBackActivityHistoryPageKeyedDataSource>()
    val sourceWithdrawalLiveData = MutableLiveData<WithdrawalHistoryPageKeyedDataSource>()
    val sourceVoucherLiveData = MutableLiveData<VoucherPurchasedHistoryPageKeyedDataSource>()
    override fun create(): DataSource<Int, CashBackHistory> {
        when (type) {
            MyCashBackPageFragment.TYPE_CASH_BACK_ACTIVITY -> {
                val source = CashBackActivityHistoryPageKeyedDataSource(api, retryExecutor)
                sourceCbActivityLiveData.postValue(source)
                return source
            }
            MyCashBackPageFragment.TYPE_WITHDRAWAL -> {
                val source = WithdrawalHistoryPageKeyedDataSource(api, retryExecutor)
                sourceWithdrawalLiveData.postValue(source)
                return source

            }
            MyCashBackPageFragment.TYPE_VOUCHERS -> {
                val source = VoucherPurchasedHistoryPageKeyedDataSource(api, retryExecutor)
                sourceVoucherLiveData.postValue(source)
                return source
            }
            else -> {
                throw IllegalStateException("Unknown promotion type")
            }
        }
    }
}