package sg.prelens.jinny.repositories.inmemory.bypage

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import retrofit2.Call
import retrofit2.Response
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.Merchant
import sg.prelens.jinny.models.MerchantResponse
import sg.prelens.jinny.repositories.NetworkState
import java.io.IOException
import java.util.concurrent.Executor

/**
 * Created by tommy on 3/16/18.
 */
class MerchantPageKeyedDataSource(
        private val api: ApiLink,
        private val retryExecutor: Executor,
        private val keyword: String) : PageKeyedDataSource<Int, Merchant>() {
    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null
    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Merchant>) {
        val request = api.getMerchants(
                page = 1,
                limit = params.requestedLoadSize,
                keyword = keyword
        )
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        // triggered by a refresh, we better execute sync
        try {
            val response = request.execute()
            val items = response.body()?.results ?: emptyList()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult( items.sortedBy { merchant -> merchant.name }, 1, 2)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Merchant>) {
        networkState.postValue(NetworkState.LOADING)
        api.getMerchants(
                page = params.key,
                limit = params.requestedLoadSize,
                keyword = keyword).enqueue(
                object : retrofit2.Callback<MerchantResponse> {
                    override fun onFailure(call: Call<MerchantResponse>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(
                            call: Call<MerchantResponse>,
                            response: Response<MerchantResponse>) {
                        if (response.isSuccessful) {
                            val items = response.body()?.results ?: emptyList()
                            retry = null
                            callback.onResult(items.sortedBy { merchant -> merchant.name }, params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue(
                                    NetworkState.error("error code: ${response.code()}"))
                        }
                    }
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Merchant>) {
    }
}