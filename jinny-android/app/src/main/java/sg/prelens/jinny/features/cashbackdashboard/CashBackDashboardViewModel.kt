package sg.prelens.jinny.features.cashbackdashboard

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory.CashBackHistoryRepository

class CashBackDashboardViewModel(repository: CashBackHistoryRepository) : ViewModel() {
    private var repoResult = repository.fetchHistory(DEFAULT_PAGE_SIZE)
    val data = repoResult.pagedList
    val networkState = repoResult.networkState
    val refreshState = repoResult.refreshState

    val error: LiveData<String?> = Transformations.map(repoResult.networkState, {
        it?.msg
    })

    fun refresh() {
        repoResult.refresh.invoke()
    }

    fun retry() {
        repoResult.retry.invoke()
    }
}