package sg.prelens.jinny.repositories.inmemory.promotion

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.features.promotion.PromotionPageFragment
import sg.prelens.jinny.features.promotion.PromotionPageRedeemedFragment
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.repositories.inmemory.promotion.promotionredeemed.PromotionRedeemedPageKeyedDataSource
import sg.prelens.jinny.repositories.inmemory.promotionarchived.PromotionArchivedPageKeyedDataSource
import sg.prelens.jinny.repositories.inmemory.promotionstarred.PromotionStarredPageKeyedDataSource
import java.util.concurrent.Executor

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/19/18<br>.
 */
class PromotionDataSourceFactory(private val api: ApiLink, private val retryExecutor: Executor, private val keyword: String, private val order: String
                                 , private val type: Int) : DataSource.Factory<Int, PromotionList> {
    val sourceLiveData = MutableLiveData<PromotionPageKeyedDataSource>()
    val sourceStarredLiveData = MutableLiveData<PromotionStarredPageKeyedDataSource>()
    val sourceArchivedLiveData = MutableLiveData<PromotionArchivedPageKeyedDataSource>()
    val sourceRedeemedLiveData = MutableLiveData<PromotionRedeemedPageKeyedDataSource>()
    override fun create(): DataSource<Int, PromotionList> {
        when (type) {
            PromotionPageFragment.TYPE_ALL -> {
                val source = PromotionPageKeyedDataSource(api, retryExecutor, keyword, order)
                sourceLiveData.postValue(source)
                return source
            }
            PromotionPageFragment.TYPE_ARCHIVED -> {
                val source = PromotionArchivedPageKeyedDataSource(api, retryExecutor, keyword, order)
                sourceArchivedLiveData.postValue(source)
                return source

            }
            PromotionPageFragment.TYPE_STARRED -> {
                val source = PromotionStarredPageKeyedDataSource(api, retryExecutor, keyword, order)
                sourceStarredLiveData.postValue(source)
                return source
            }
            PromotionPageFragment.TYPE_REDEEMED -> {
                val source = PromotionRedeemedPageKeyedDataSource(api, retryExecutor, keyword, order)
                sourceRedeemedLiveData.postValue(source)
                return source
            }
            else -> {
                throw IllegalStateException("Unknown promotion type")
            }
        }
    }
}