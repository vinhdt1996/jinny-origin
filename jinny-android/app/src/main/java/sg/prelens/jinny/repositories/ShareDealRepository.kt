package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.Voucher
import sg.prelens.jinny.utils.RetrofitLiveData

class ShareDealRepository(val apiService: ApiLink) {

    fun addShareDeal(id: String?, error: MutableLiveData<Throwable>): LiveData<ShareDealResponse> =
            Transformations.map(RetrofitLiveData(apiService.putShareDeal(id), error)) { it }
}