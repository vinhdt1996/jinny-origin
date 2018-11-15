package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.CashBackOverView
import sg.prelens.jinny.models.CashBackOverViewResponse
import sg.prelens.jinny.models.MyProfile
import sg.prelens.jinny.utils.ApiResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class CashBackOverViewRepository(val apiService: ApiLink) {
    fun fetchCashBackOverView(): LiveData<ApiResponse<CashBackOverView?>> =
            Transformations.map(apiService.fetchCashBackInformation()) { it.map { it?.result } }
}