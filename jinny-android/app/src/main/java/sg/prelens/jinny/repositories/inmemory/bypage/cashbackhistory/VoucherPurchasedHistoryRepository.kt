package sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.features.cashbackdashboard.MyCashBackPageFragment
import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.repositories.Listing
import java.util.concurrent.Executor

class VoucherPurchasedHistoryRepository(private val api: ApiLink,
                                        private val networkExecutor: Executor) : CashBackHistoryRepository {
    @MainThread
    override fun fetchHistory(pageSize: Int): Listing<CashBackHistory> {
        val sourceFactory = CashBackDashboardHistoryDataSourceFactory(api, networkExecutor, MyCashBackPageFragment.TYPE_VOUCHERS)

        val config = PagedList
                .Config
                .Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize)
                .build()
        val livePagedList = LivePagedListBuilder(sourceFactory, config)
                // provide custom executor for network requests, otherwise it will default to
                // Arch Components' IO pool which is also used for disk access
                .setBackgroundThreadExecutor(networkExecutor)
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceVoucherLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceVoucherLiveData, {
                    it.networkState
                }),
                retry = {
                    sourceFactory.sourceVoucherLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceVoucherLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }

}
