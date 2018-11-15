package sg.prelens.jinny.repositories.inDB

import android.arch.paging.PagedList
import android.support.annotation.MainThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.prelens.jinny.PagingRequestHelper
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.db.entity.VoucherEntity
import sg.prelens.jinny.models.PromotionResponse
import sg.prelens.jinny.utils.createStatusLiveData
import java.util.concurrent.Executor

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/11/18<br>.
 */
class VoucherBoundaryCallback(private val keyword: String,
                              private val webservice: ApiLink,
                              private val handleResponse: (String, PromotionResponse?) -> Unit,
                              private val ioExecutor: Executor,
                              private val networkPageSize: Int)
    : PagedList.BoundaryCallback<VoucherEntity>() {
    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()
    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getPromotions(
                    keyword = keyword,
                    page = 1,
                    perPage = networkPageSize,
                    order = "ASC").enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: VoucherEntity) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getPromotions(
                    keyword = keyword,
                    page = 1,
                    perPage = networkPageSize,
                    order = "ASC").enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
            response: Response<PromotionResponse>,
            it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(keyword, response.body())
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: VoucherEntity) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<PromotionResponse> {
        return object : Callback<PromotionResponse> {
            override fun onFailure(
                    call: Call<PromotionResponse>,
                    t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                    call: Call<PromotionResponse>,
                    response: Response<PromotionResponse>) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}
