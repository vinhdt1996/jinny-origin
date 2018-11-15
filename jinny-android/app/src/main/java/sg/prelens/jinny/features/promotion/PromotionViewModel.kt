package sg.prelens.jinny.features.promotion

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import sg.prelens.jinny.constant.DEFAULT_PAGE_SIZE
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.repositories.Listing
import sg.prelens.jinny.repositories.PromotionOperationRepository
import sg.prelens.jinny.repositories.PromotionRepository
import sg.prelens.jinny.utils.AbsentLiveData
import sg.prelens.jinny.utils.ExecutorLiveData
import java.util.concurrent.Executor


class PromotionViewModel(val repository: PromotionRepository, val operationModel: PromotionOperationRepository?, val repoFilter: PromotionRepository.PromotionFilterRepository?) : ViewModel() {
    private val filterRequest = MutableLiveData<Pair<String, String>?>()
    private var repoResult = Listing.absent<PromotionList>()
    val networkState = repoResult.networkState
    private val bookmarkRequest = MutableLiveData<Pair<String?, Int?>?>()
    val refreshState = repoResult.refreshState
    val bookmarkLiveData: LiveData<AddBookmarkResonse>
    val bookmarkErrorRequest = MutableLiveData<Throwable>()
    private val archiveRequest = MutableLiveData<Pair<String?, Int?>?>()
    val archiveLiveData: LiveData<DefaultResonse>
    val archiveErrorRequest = MutableLiveData<Throwable>()

    fun bookmarkVoucher(id: String?, users_voucher_id: Int?) {
        this.bookmarkRequest.value = Pair(id, users_voucher_id)
    }

    fun archiveVoucher(id: String?, users_voucher_id: Int?) {
        this.archiveRequest.value = Pair(id, users_voucher_id)
    }

    val error: LiveData<String?> = Transformations.map(repoResult.networkState) {
        it?.msg
    }

    val errorBookMark: LiveData<Throwable> = Transformations.map(bookmarkErrorRequest) {
        it
    }

    val errorArchive: LiveData<Throwable> = Transformations.map(archiveErrorRequest) {
        it
    }

    var promotions = Transformations.switchMap<Pair<String, String>, PagedList<PromotionList>>(filterRequest) {
        repoResult = repository.fetchPromotion(it?.first ?: "", DEFAULT_PAGE_SIZE, it?.second
                ?: "recent")
        repoResult.pagedList
    }!!

    //Cr6 feature deal
    var featureRequest = MutableLiveData<PagedList<PromotionList>>()
    var featureResult: LiveData<List<PromotionList>>
    var otherRequest = MutableLiveData<PagedList<PromotionList>>()
    var otherResult: LiveData<List<PromotionList>>

    fun filter(keyword: String = "", order: String = "recent") {
        filterRequest.value = Pair(keyword, order)
    }

    fun refresh() {
        repoResult.refresh.invoke()
    }

    fun retry() {
        repoResult.retry.invoke()
    }

    var filterPageRequest = MutableLiveData<PagedList<PromotionList>>()

    fun filterPageList(pagedList: PagedList<PromotionList>) {
        filterPageRequest.value = pagedList
        //Cr6 request feature deal
        featureRequest.value = pagedList
        otherRequest.value = pagedList
    }

    val filterPageListResult: LiveData<List<PromotionList>>

    init {
        bookmarkLiveData = Transformations.switchMap<Pair<String?, Int?>, AddBookmarkResonse>(bookmarkRequest) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                operationModel?.bookmarkVoucher(it.first ?: "", it.second
                        ?: 0, bookmarkErrorRequest)
            }
        }

        archiveLiveData = Transformations.switchMap<Pair<String?, Int?>, DefaultResonse>(archiveRequest) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                operationModel?.archiveVoucher(it.first ?: "", it.second ?: 0, archiveErrorRequest)
            }
        }

        filterPageListResult = Transformations.switchMap(filterPageRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repoFilter?.filter(it)
            }
        })

        featureResult = Transformations.switchMap(featureRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repoFilter?.filterFeatureDeal(it)
            }
        })

        otherResult = Transformations.switchMap(featureRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repoFilter?.filterOtherDeal(it)
            }
        })

    }
}