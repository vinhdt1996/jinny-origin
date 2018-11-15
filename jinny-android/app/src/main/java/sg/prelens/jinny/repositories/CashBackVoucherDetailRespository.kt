package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.R.string.email
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.VoucherPurchaseDetailResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class CashBackVoucherDetailRespository(val apiService: ApiLink) {
    fun fetchCashBackDetail(id: String, error: MutableLiveData<Throwable>): LiveData<VoucherPurchaseDetailResponse> = RetrofitLiveData(apiService.fetchPurchasedVoucherDetail(id), error)
}