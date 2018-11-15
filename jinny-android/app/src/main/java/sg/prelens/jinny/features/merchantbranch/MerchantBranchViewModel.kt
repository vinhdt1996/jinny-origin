package sg.prelens.jinny.features.merchantbranch

import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.MerchantBranch
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.MerchantBranchRepository
import java.util.concurrent.Executor

class MerchantBranchViewModel(val id: Int, repository: MerchantBranchRepository, val executor: Executor) : ViewModel() {
    private val merchantBranchLiveData: Listing<MerchantBranch> = repository.fetchMerchantBranch(id, DEFAULT_PAGE_SIZE)
    val merchantBranch = merchantBranchLiveData.pagedList
    val networkState = merchantBranchLiveData.networkState
    val refreshState = merchantBranchLiveData.refreshState
    fun refresh() {
        merchantBranchLiveData.refresh.invoke()
    }

    fun retry() {
        merchantBranchLiveData.retry.invoke()
    }
}