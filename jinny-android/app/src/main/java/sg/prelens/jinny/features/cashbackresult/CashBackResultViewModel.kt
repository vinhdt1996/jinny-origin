package sg.prelens.jinny.features.cashbackresult

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.repositories.CashBackResultRepository
import sg.prelens.jinny.utils.AbsentLiveData

class CashBackResultViewModel(repository: CashBackResultRepository) : ViewModel() {
    private val request = MutableLiveData<Triple<String, Int, String>>()
    val response: LiveData<DefaultResonse>
    val errorLiveData = MutableLiveData<Throwable>()

    fun setUpdatePhoto(id: String, users_voucher_id: Int, photo: String) {
        this.request.value = Triple(id, users_voucher_id, photo)
    }

    init {
        response = Transformations.switchMap<Triple<String, Int, String>, DefaultResonse>(request) {
            if (it == null) {
                AbsentLiveData.create()
            } else
                repository.requestCashBack(it.first, it.second, it.third, errorLiveData)
        }
    }
}