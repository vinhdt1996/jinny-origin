package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.utils.RetrofitLiveData
import java.io.File

class CashBackResultRepository(val apiService: ApiLink) {

    fun requestCashBack(id: String, users_voucher_id: Int, photo: String, error: MutableLiveData<Throwable>): LiveData<DefaultResonse> {
        val file = File(photo)
        val requestBody = RequestBody.create(
                MediaType.parse("image/*"),
                file
        )
        val body = MultipartBody.Part.createFormData("cashback_image", "image", requestBody)
        return RetrofitLiveData(apiService.requestCashBack(id, users_voucher_id, body), error)
    }
}
