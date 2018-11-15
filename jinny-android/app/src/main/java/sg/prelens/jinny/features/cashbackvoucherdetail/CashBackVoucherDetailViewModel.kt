package sg.prelens.jinny.features.cashbackvoucherdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.VoucherPurchaseDetailResponse
import sg.prelens.jinny.repositories.CashBackVoucherDetailRespository
import sg.prelens.jinny.utils.AbsentLiveData

class CashBackVoucherDetailViewModel(repository: CashBackVoucherDetailRespository) : ViewModel() {
    private val cashBackRequest = MutableLiveData<String>()
    val cashBackLiveData: LiveData<VoucherPurchaseDetailResponse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun setCashBackVoucherDetailID(id: String) {
        this.cashBackRequest.value = id
    }

    init {
        cashBackLiveData = Transformations.switchMap<String, VoucherPurchaseDetailResponse>(cashBackRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.fetchCashBackDetail(it, errorLiveData)
                })
    }
}