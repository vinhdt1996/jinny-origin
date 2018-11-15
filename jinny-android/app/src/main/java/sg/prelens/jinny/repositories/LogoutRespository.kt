package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.LogoutResponse
import sg.prelens.jinny.utils.RetrofitLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/16/18<br>.
 */
class LogoutRespository(val apiService: ApiLink) {
    fun logoutAccount(token: String, error:MutableLiveData<Throwable>): LiveData<LogoutResponse> = RetrofitLiveData(apiService.logoutAccount(token),error)
}