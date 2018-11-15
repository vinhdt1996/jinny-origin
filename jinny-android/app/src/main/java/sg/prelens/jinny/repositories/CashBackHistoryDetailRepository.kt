package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.utils.RetrofitLiveData

class CashBackHistoryDetailRepository(val apiService: ApiLink) {

    fun fetchCashBackDetail(id: String?, error: MutableLiveData<Throwable>): LiveData<CashBackHistory> =
            Transformations.map(RetrofitLiveData(apiService.getCashBackHistoryDetail(id), error)) { it.result }
}