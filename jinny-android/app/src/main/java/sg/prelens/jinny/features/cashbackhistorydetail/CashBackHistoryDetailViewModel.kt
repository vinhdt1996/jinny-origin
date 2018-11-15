package sg.prelens.jinny.features.cashbackhistorydetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.repositories.CashBackHistoryDetailRepository
import sg.prelens.jinny.utils.AbsentLiveData

class CashBackHistoryDetailViewModel(repository: CashBackHistoryDetailRepository) : ViewModel() {

    val error = MutableLiveData<Throwable>()
    val request = MutableLiveData<String>()

    val result: LiveData<CashBackHistory>

    fun requestData(id: String) {
        request.value = id
    }

    init {
        result = Transformations.switchMap(request, {
            if (it == null) {
                AbsentLiveData.create()
            } else
                repository.fetchCashBackDetail(it, error)
        })
    }
}