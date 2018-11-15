package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.VoucherResponse
import sg.prelens.jinny.utils.RetrofitLiveData

class AddQrCodeRepository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun addQrCode(code: String): LiveData<VoucherResponse> = RetrofitLiveData(apiService.addQrCode(code), error)
}
