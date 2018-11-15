package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.RedeemCashBackResponse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.utils.RetrofitLiveData

class BankAccountRepository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun fetchBankAccountResult(): LiveData<RedeemCashBackResponse> = RetrofitLiveData(apiService.getBankAccountResult(), error)

    fun removeBankAccount(id: String, error: MutableLiveData<Throwable>): LiveData<DefaultResonse> = RetrofitLiveData(apiService
            .removeBankAccount(id), error)

    fun postMaillingPurchase(voucher_id: String?): LiveData<DefaultResonse> =
            RetrofitLiveData(apiService.postMaillingPurchase(voucher_id ?: ""), error)
}