package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.BankInformationResponse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.utils.RetrofitLiveData

class WithDrawConfirmationRepository(val apiService: ApiLink) {
    fun getBankInformation(id: Int?, error: MutableLiveData<Throwable>): LiveData<BankInformationResponse> = RetrofitLiveData(apiService.getBankInformation(id
            ?: -1), error)

    fun postWithDrawConfrmation(id: String?, amount: String?, error: MutableLiveData<Throwable>): LiveData<DefaultResonse> = RetrofitLiveData(apiService.postWithDrawConformation(id
            ?: "", amount ?: ""), error)
}