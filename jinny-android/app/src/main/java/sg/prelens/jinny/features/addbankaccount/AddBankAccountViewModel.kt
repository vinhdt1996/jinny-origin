package sg.prelens.jinny.features.addbankaccount

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.BankInformation
import sg.prelens.jinny.models.BankInformationResponse
import sg.prelens.jinny.models.BankNameRespone
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.repositories.AddBankAccountRespository
import sg.prelens.jinny.utils.AbsentLiveData

class AddBankAccountViewModel(repository: AddBankAccountRespository) : ViewModel() {
    private val addBankAccountRepository = MutableLiveData<BankInformation?>()
    private val editBankAccountRepository = MutableLiveData<Pair<Int?, BankInformation?>>()
    val addBankAccountLiveData: LiveData<BankInformationResponse>
    val listBankAccountLiveData: LiveData<BankNameRespone> = repository.getBankName()
    val editBankAccountLiveData: LiveData<DefaultResonse>
    val errorLiveData: LiveData<Throwable>

    fun addBankAccount(bankInformation: BankInformation?) {
        this.addBankAccountRepository.value = bankInformation
    }

    fun editBankAccount(id: Int?, bankInformation: BankInformation?) {
        this.editBankAccountRepository.value = Pair(id, bankInformation)
    }

    init {
        addBankAccountLiveData = Transformations.switchMap(addBankAccountRepository) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.addBankAccount(it)
            }
        }
        editBankAccountLiveData = Transformations.switchMap(editBankAccountRepository) {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.editBankAccount(it.first, it.second)
            }
        }

        errorLiveData = repository.error
    }
}