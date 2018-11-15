package sg.prelens.jinny.features.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import sg.prelens.jinny.models.ChangeFirebaseResponse
import sg.prelens.jinny.models.DefaultResonse
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.repositories.AuthRepository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Created by tommy on 3/13/18.
 */
class SignUpViewModel(repository: AuthRepository) : ViewModel() {
    private val loginRequest = MutableLiveData<Pair<String, String>?>()

    val userLiveData: LiveData<ProfileUser>

    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    private val firebaseToken = MutableLiveData<String?>()
    fun setLogin(email: String, password: String) {
        this.loginRequest.value = Pair(email, password)
    }
    init {
        userLiveData = Transformations.switchMap<Pair<String, String>, ProfileUser>(loginRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.signUp(it.first, it.second, errorLiveData, FirebaseInstanceId.getInstance().token ?: "")
            }
        })
    }
}
