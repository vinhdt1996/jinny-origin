package sg.prelens.jinny.repositories.inmemory.bypage

import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.MerchantBranch
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.MerchantBranchRepository
import java.util.concurrent.Executor

/**
 * Created by vinova on 1/17/18.
 */
/**
 * Repository implementation that returns a Listing that loads data directly from network by using
 * the previous / next page keys returned in the query.
 */
class MerchantBranchInMemoryByPageKeyedRepository(private val api: ApiLink,
                                                  private val networkExecutor: Executor) : MerchantBranchRepository {
    @MainThread
    override fun fetchMerchantBranch(id: Int, pageSize: Int): Listing<MerchantBranch> {
        val sourceFactory = MerchantBranchDataSourceFactory(api, networkExecutor, id)

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

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
                pagedList = livePagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveData, {
                    it.networkState
                }),
                retry = {
                    sourceFactory.sourceLiveData.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveData.value?.invalidate()
                },
                refreshState = refreshState
        )
    }

}
