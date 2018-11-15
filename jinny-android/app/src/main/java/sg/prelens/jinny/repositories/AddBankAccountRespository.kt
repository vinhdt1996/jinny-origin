package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.BankInformation
import sg.prelens.jinny.models.BankInformationResponse
import sg.prelens.jinny.models.BankNameRespone
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.utils.RetrofitLiveData

class AddBankAccountRespository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun addBankAccount(bankInformation: BankInformation?): LiveData<BankInformationResponse> =
            RetrofitLiveData(apiService.addBankAccount(bankInformation?.holder_name, bankInformation?.bank_name, bankInformation?.account_number), error)

    fun editBankAccount(id: Int?, bankInformation: BankInformation?): LiveData<DefaultResonse> =
            RetrofitLiveData(apiService.editBankAccount(id ?: 0, bankInformation?.holder_name, bankInformation?.bank_name, bankInformation?.account_number), error)

    fun getBankName(): LiveData<BankNameRespone> =
            RetrofitLiveData(apiService.getBankName(), error)
}