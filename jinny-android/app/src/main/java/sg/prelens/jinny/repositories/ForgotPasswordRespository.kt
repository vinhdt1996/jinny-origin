package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.ForgotPasswordResponse
import sg.prelens.jinny.utils.RetrofitLiveData

/*
 * Author : BIMBIM<br>.
 * Create Date : 3/15/18<br>.
 */
class ForgotPasswordRespository(val apiService: ApiLink) {
    fun forgotPassword(email: String, error: MutableLiveData<Throwable>): LiveData<ForgotPasswordResponse> = RetrofitLiveData(apiService.forgotPassword(email),error)
}