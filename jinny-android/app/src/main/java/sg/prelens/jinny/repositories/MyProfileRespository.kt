package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.MyProfile
import sg.prelens.jinny.utils.RetrofitLiveData

class MyProfileRespository(val apiService: ApiLink) {
    fun getMyProfile(error: MutableLiveData<Throwable>): LiveData<MyProfile> = RetrofitLiveData(apiService.getMyProfile(), error)
}