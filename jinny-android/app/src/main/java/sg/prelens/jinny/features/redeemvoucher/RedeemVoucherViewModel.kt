package sg.prelens.jinny.features.redeemvoucher

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.PromotionDetailResponse
import sg.prelens.jinny.repositories.RedeemVoucherRepository
import sg.prelens.jinny.utils.AbsentLiveData
import java.util.concurrent.Executor

class RedeemVoucherViewModel(val repository: RedeemVoucherRepository,
                             val executor: Executor) : ViewModel() {
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    private val redeemId = MutableLiveData<Pair<String?, Int?>?>()
    val redeemLiveData: LiveData<PromotionDetailResponse>

    fun redeemVoucher(id: String?, users_voucher_id: Int?) {
        this.redeemId.value = Pair(id, users_voucher_id)
    }

    init {
        redeemLiveData = Transformations.switchMap<Pair<String?, Int?>, PromotionDetailResponse>(redeemId, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.redeemVoucher(it.first ?: "", it.second ?: 0, errorLiveData)
            }
        })
    }
}