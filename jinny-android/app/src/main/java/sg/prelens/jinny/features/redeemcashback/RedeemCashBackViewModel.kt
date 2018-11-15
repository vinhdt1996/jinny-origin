package sg.prelens.jinny.features.redeemcashback

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.RedeemCashBackResponse
import sg.prelens.jinny.repositories.BankAccountRepository
import sg.prelens.jinny.utils.AbsentLiveData

class RedeemCashBackViewModel(repository: BankAccountRepository) : ViewModel() {
    var redeemCashBackResult: LiveData<RedeemCashBackResponse> = repository.fetchBankAccountResult()
    var postMaillingPurchaseRepository = MutableLiveData<String?>()
    val postMaillingPurchaseResult: LiveData<DefaultResonse>
    val errorLiveData: MutableLiveData<Throwable>

    fun postIdPurchase(id: String?) {
        postMaillingPurchaseRepository.value = id ?: ""
    }

    init {
        postMaillingPurchaseResult = Transformations.switchMap(postMaillingPurchaseRepository, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.postMaillingPurchase(it)
            }
        })
        errorLiveData = repository.error
    }
}