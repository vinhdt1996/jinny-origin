package sg.prelens.jinny.features.withdrawcashback

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.repositories.BankAccountRepository
import sg.prelens.jinny.utils.AbsentLiveData

class WithDrawCashBackViewModel(repository: BankAccountRepository) : ViewModel() {
    val defaultResonse: LiveData<DefaultResonse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    private var id = MutableLiveData<String>()

    public fun removeBankAccount(id: String) {
        this.id.value = id
    }

    init {
        defaultResonse = Transformations.switchMap<String, DefaultResonse>(id, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.removeBankAccount(it, errorLiveData)
            }
        })
    }

}
