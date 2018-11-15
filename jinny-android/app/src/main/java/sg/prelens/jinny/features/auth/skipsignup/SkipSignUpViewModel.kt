package sg.prelens.jinny.features.auth.skipsignup

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.google.firebase.iid.FirebaseInstanceId
import sg.prelens.jinny.models.ForgotPasswordResponse
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.models.SkipSignUpResponse
import sg.prelens.jinny.repositories.AuthRepository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/25/18<br>.
 */
class SkipSignUpViewModel(repository: AuthRepository) : ViewModel() {
    private val signUpGuestRequest = MutableLiveData<String>()
    val signUpGuestLiveData: LiveData<ProfileUser>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    fun setSignUpGuestPassword(deviceID: String) {
        this.signUpGuestRequest.value = deviceID
    }

    init {
        signUpGuestLiveData = Transformations.switchMap<String, ProfileUser>(signUpGuestRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.skipSignUp(it, errorLiveData, FirebaseInstanceId.getInstance().token ?: "")
            }
        })
    }
}