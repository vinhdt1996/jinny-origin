package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.utils.ExecutorLiveData
import java.util.concurrent.Executor

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/19/18<br>.
 */
interface PromotionRepository {
    fun fetchPromotion(keyword: String, pageSize: Int, order: String): Listing<PromotionList>

    interface PromotionFilterRepository {
        fun filter(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>>

        fun filterFeatureDeal(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>>

        fun filterOtherDeal(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>>
    }
}

class PromotionFilter(private val executor: Executor) : PromotionRepository.PromotionFilterRepository {
    override fun filterOtherDeal(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>> =
            ExecutorLiveData(executor) {
                pagedList.filter { it.is_feature.not() }
            }


    override fun filterFeatureDeal(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>> =
            ExecutorLiveData(executor) {
                pagedList.filter { it.is_feature }
            }

    override fun filter(pagedList: PagedList<PromotionList>): LiveData<List<PromotionList>> =
            ExecutorLiveData(executor) {
                pagedList.filter { it.is_read.not() }
            }

}

