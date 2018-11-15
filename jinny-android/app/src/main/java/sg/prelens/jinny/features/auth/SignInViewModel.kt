package sg.prelens.jinny.features.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.google.firebase.iid.FirebaseInstanceId
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.repositories.AuthRepository
import sg.prelens.jinny.service.JFirebaseInstanceIDService
import sg.prelens.jinny.utils.AbsentLiveData

class SignInViewModel(repository: AuthRepository) : ViewModel() {
    private val loginRequest = MutableLiveData<Triple<String, String, String>?>()

    val userLiveData: LiveData<ProfileUser>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun setLogin(email: String, password: String ) {
        this.loginRequest.value = Triple(email, password,
                FirebaseInstanceId.getInstance().token ?: "" )
    }

    init {
        userLiveData = Transformations.switchMap<Triple<String, String, String>, ProfileUser>(loginRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.signIn(it.first, it.second,it.third, errorLiveData)
            }
        })
    }
}