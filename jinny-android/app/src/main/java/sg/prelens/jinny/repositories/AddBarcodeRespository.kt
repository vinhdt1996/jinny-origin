package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBarcodeResponse
import sg.prelens.jinny.models.ChangePasswordResponse
import sg.prelens.jinny.models.MembershipDetailResponse
import sg.prelens.jinny.utils.RetrofitLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class AddBarcodeRespository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun addBarcode(code: String, merchantID: Int): LiveData<MembershipDetailResponse> = RetrofitLiveData(apiService.addBarcode(code, merchantID), error)
}
