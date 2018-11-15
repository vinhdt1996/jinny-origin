package sg.prelens.jinny.features.editprofile

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.MyProfile
import sg.prelens.jinny.repositories.MyProfileRespository

class MyProfileViewModel(repository: MyProfileRespository) : ViewModel() {
    val myProfileLiveData: LiveData<MyProfile>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    init {
        myProfileLiveData = repository.getMyProfile(errorLiveData)
    }
}