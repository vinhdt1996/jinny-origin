package sg.prelens.jinny.features.auth.setupjinnyaccount

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.repositories.AuthRepository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/27/18<br>.
 */
class SetupJinnyAccountViewModel(repository: AuthRepository) : ViewModel() {
    private val signUpGuestRequest = MutableLiveData<Pair<String, String>?>()
    val signUpGuestLiveData: LiveData<ProfileUser>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    fun setSignUpGuest(email: String, password: String) {
        this.signUpGuestRequest.value = Pair(email, password)
    }

    init {
        signUpGuestLiveData = Transformations.switchMap<Pair<String, String>, ProfileUser>(signUpGuestRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.signUpGuest(it.first, it.second, errorLiveData)
            }
        })
    }
}