package sg.prelens.jinny.repositories.inmemory.bypage

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.MerchantBranch
import java.util.concurrent.Executor

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class MerchantBranchDataSourceFactory(
        private val api: ApiLink,
        private val retryExecutor: Executor,
        private val id: Int) : DataSource.Factory<Int, MerchantBranch> {
    val sourceLiveData = MutableLiveData<MerchantBranchPageKeyedDataSource>()
    override fun create(): DataSource<Int, MerchantBranch> {
        val source = MerchantBranchPageKeyedDataSource(api, retryExecutor, id)
        sourceLiveData.postValue(source)
        return source
    }
}