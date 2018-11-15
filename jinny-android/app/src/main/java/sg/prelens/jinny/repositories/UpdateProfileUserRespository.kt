package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.models.UpdateProfileUser
import sg.prelens.jinny.utils.RetrofitLiveData

class UpdateProfileUserRespository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun updateProfile(profileUser: ProfileUser?):
            LiveData<UpdateProfileUser> = RetrofitLiveData(apiService.updateProfile( profileUser?.email, profileUser?.dob, profileUser?.gender, profileUser?.residential_region?.id), error)
}