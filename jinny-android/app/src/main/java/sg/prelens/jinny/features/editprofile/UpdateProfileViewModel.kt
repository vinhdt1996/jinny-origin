package sg.prelens.jinny.features.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.ChangePasswordResponse
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.models.UpdateProfileUser
import sg.prelens.jinny.repositories.UpdateProfileUserRespository
import sg.prelens.jinny.utils.AbsentLiveData

class UpdateProfileViewModel(/*profileUser: ProfileUser?,*/ repository: UpdateProfileUserRespository) : ViewModel() {
    private val updateProfileRequest = MutableLiveData<ProfileUser>()
    val updateProfileLiveData: LiveData<UpdateProfileUser>
    val errorLiveData: LiveData<Throwable>
    fun setUpdateProfile(profileUser: ProfileUser?) {
        this.updateProfileRequest.value = profileUser
    }

    init {
        //updateProfileLiveData = repository.updateProfile(profileUser, errorLiveData)
        updateProfileLiveData = Transformations.switchMap<ProfileUser, UpdateProfileUser>(updateProfileRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.updateProfile(it)
                })
        errorLiveData = repository.error
    }
}