package sg.prelens.jinny.features.withdrawconfirmation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.BankInformation
import sg.prelens.jinny.models.BankInformationResponse
import sg.prelens.jinny.models.RedeemCashBackResponse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.repositories.WithDrawConfirmationRepository
import sg.prelens.jinny.utils.AbsentLiveData

class WithDrawConfirmationViewModel(repository: WithDrawConfirmationRepository) : ViewModel() {
    private val bankInformationRepository = MutableLiveData<Int>()
    private val withDrawConfirmationRepository = MutableLiveData<Pair<String, String>>()
    val bankInformation: LiveData<BankInformationResponse>
    val withDrawConfirmation: LiveData<DefaultResonse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val errorLiveData2: MutableLiveData<Throwable> = MutableLiveData()

    fun setbankInformationRepository(id: Int?) {
        bankInformationRepository.value = id ?: -1
    }

    fun setWithDrawConfirmationRepository(id: String?, amount: String?) {
        withDrawConfirmationRepository.value = Pair(id ?: "", amount ?: "")
    }

    init {
        bankInformation = Transformations.switchMap(bankInformationRepository, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.getBankInformation(it, errorLiveData2)
            }
        })
        withDrawConfirmation = Transformations.switchMap(withDrawConfirmationRepository, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.postWithDrawConfrmation(it.first, it.second, errorLiveData)
            }
        })
        //errorLiveData = repository.error
    }
}