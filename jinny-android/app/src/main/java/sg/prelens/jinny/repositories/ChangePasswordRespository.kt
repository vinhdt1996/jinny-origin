package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.ChangePasswordResponse
import sg.prelens.jinny.utils.RetrofitLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/18/18<br>.
 */
class ChangePasswordRespository(val apiService: ApiLink) {
    fun changePassword(currentPassword: String, newPassword: String, error:MutableLiveData<Throwable>): LiveData<ChangePasswordResponse> = RetrofitLiveData(apiService.changePassword(currentPassword, newPassword),error)
}