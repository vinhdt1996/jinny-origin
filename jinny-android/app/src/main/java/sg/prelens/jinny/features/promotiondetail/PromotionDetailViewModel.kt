package sg.prelens.jinny.features.promotiondetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.RemoveVoucherResponse
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.repositories.PromotionDetailRepository
import sg.prelens.jinny.utils.AbsentLiveData
import java.util.concurrent.Executor

class PromotionDetailViewModel(val repository: PromotionDetailRepository,
                               val executor: Executor) : ViewModel() {
    private val promotionRequest = MutableLiveData<Pair<String?, Int?>?>()
    val promotionDetailLiveData: LiveData<Voucher>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    private val bookmarkId = MutableLiveData<Pair<String?, Int?>?>()
    val bookmarkLiveData: LiveData<AddBookmarkResonse>
    private val removeId = MutableLiveData<Pair<String?, Int?>?>()
    val removeLiveData: LiveData<RemoveVoucherResponse>
    private val archiveRequest = MutableLiveData<Pair<String?, Int?>?>()
    val archiveLiveData: LiveData<DefaultResonse>
    val archiveErrorRequest = MutableLiveData<Throwable>()

    fun setVoucherId(id: String, users_voucher_id: Int?) {
        this.promotionRequest.value = Pair(id, users_voucher_id)
    }

    fun setBookmarked(id: String?, users_voucher_id: Int?) {
        this.bookmarkId.value = Pair(id, users_voucher_id)
    }

    fun archiveVoucher(id: String?, users_voucher_id: Int?) {
        this.archiveRequest.value = Pair(id, users_voucher_id)
    }

    fun removeVoucher(id: String?, users_voucher_id: Int?) {
        this.removeId.value = Pair(id, users_voucher_id)
    }

    init {
        promotionDetailLiveData = Transformations.switchMap<Pair<String?, Int?>, Voucher>(promotionRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else {
                        repository.fetchPromotionDetail(it.first ?: "", it.second
                                ?: 0, errorLiveData)
                    }
                })
        bookmarkLiveData = Transformations.switchMap<Pair<String?, Int?>, AddBookmarkResonse>(bookmarkId, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.bookmarkVoucher(it.first ?: "", it.second ?: 0, errorLiveData)
            }
        })

        archiveLiveData = Transformations.switchMap<Pair<String?, Int?>, DefaultResonse>(archiveRequest,{
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.archiveVoucher(it.first ?: "", it.second ?: 0,archiveErrorRequest)
            }
        })

        removeLiveData = Transformations.switchMap<Pair<String?, Int?>, RemoveVoucherResponse>(removeId, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.removeVoucher(it.first ?: "", it.second ?: 0, errorLiveData)
            }
        })
    }
}
