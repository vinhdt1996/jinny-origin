package sg.prelens.jinny.repositories.inmemory.promotion.promotionredeemed

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import retrofit2.Call
import retrofit2.Response
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.models.PromotionResponse
import sg.prelens.jinny.repositories.NetworkState
import java.io.IOException
import java.util.concurrent.Executor

class PromotionRedeemedPageKeyedDataSource(private val api: ApiLink, private val retryExecutor: Executor, private val keyword: String, private val order: String) : PageKeyedDataSource<Int, PromotionList>() {

    private var retry: (() -> Any)? = null

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

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PromotionList>) {
        val request = api.getRedeemedVouchers(
                keyword = keyword,
                page = 1,
                perPage = params.requestedLoadSize,
                order = order
        )
        // triggered by a refresh, we better execute sync
        try {
            val response = request.execute()
            val items = response.body()?.results ?: emptyList()
            retry = null
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(items, 1, 2)
        } catch (ioException: IOException) {
            retry = {
                loadInitial(params, callback)
            }
            val error = NetworkState.error(ioException.message ?: "unknown error")
            networkState.postValue(error)
            initialLoad.postValue(error)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PromotionList>) {
        networkState.postValue(NetworkState.LOADING)
        api.getRedeemedVouchers(
                keyword = keyword,
                page = params.key,
                perPage = params.requestedLoadSize,
                order = order).enqueue(
                object : retrofit2.Callback<PromotionResponse> {
                    override fun onFailure(call: Call<PromotionResponse>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(call: Call<PromotionResponse>, response: Response<PromotionResponse>) {
                        if (response.isSuccessful) {
                            val items = response.body()?.results ?: emptyList()
                            retry = null
                            callback.onResult(items, params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                            initialLoad.postValue(NetworkState.LOADED)
                        } else {
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue(NetworkState.error("error code: ${response.code()}"))
                        }
                    }
                }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PromotionList>) {
    }
}