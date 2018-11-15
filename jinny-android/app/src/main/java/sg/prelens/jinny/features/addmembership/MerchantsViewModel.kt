package sg.prelens.jinny.features.addmembership

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.Merchant
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.MerchantRepository

/**
 * Created by tommy on 3/16/18.
 */
class MerchantsViewModel(repository: MerchantRepository) : ViewModel() {
    private val filterRequest = MutableLiveData<String>()
    private var repoResult = Listing.absent<Merchant>()
    val errorLiveData = repository.error()

    fun filter(keyword: String = "") {
        filterRequest.value = keyword
    }

    val networkState = repoResult.networkState
    val refreshState = repoResult.refreshState

    fun refresh() {
        repoResult.refresh.invoke()
    }

    fun retry() {
        repoResult.retry.invoke()
    }
}