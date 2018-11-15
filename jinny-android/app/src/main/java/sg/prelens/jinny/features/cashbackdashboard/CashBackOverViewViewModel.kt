package sg.prelens.jinny.features.cashbackdashboard

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.CashBackOverView
import sg.prelens.jinny.repositories.CashBackOverViewRepository
import sg.prelens.jinny.utils.ApiResponse

class CashBackOverViewViewModel(repository: CashBackOverViewRepository) : ViewModel() {
    val cashBackOverViewLiveData: LiveData<ApiResponse<CashBackOverView?>> = repository.fetchCashBackOverView()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun refresh() {
        cashBackOverViewLiveData.value?.refresh?.invoke()
    }

    fun retry() {
        cashBackOverViewLiveData.value?.retry?.invoke()
    }
}