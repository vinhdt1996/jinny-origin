package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Build
import sg.prelens.jinny.R.string.email
import sg.prelens.jinny.R.string.password
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.models.ChangeFirebaseResponse
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.utils.RetrofitLiveData

class AuthRepository(val apiService: ApiLink) {
    fun signIn(email: String, password: String, token: String, error: MutableLiveData<Throwable>):
            LiveData<ProfileUser> = Transformations.map(RetrofitLiveData(apiService.signIn(email, password, token,
            Build.ID), error)) { it.result }

    fun signUp(email: String, password: String, error: MutableLiveData<Throwable>, token: String):
            LiveData<ProfileUser> = Transformations.map(RetrofitLiveData(apiService.signUp(email, password, token), error)) { it.result }

    fun changeFirebaseToken(oldToken: String, newToken: String, error: MutableLiveData<Throwable>): LiveData<ChangeFirebaseResponse> = Transformations.map(RetrofitLiveData(apiService.changeToken(oldToken, newToken), error)) { it }

    fun skipSignUp(email: String, error: MutableLiveData<Throwable>, token: String):
            LiveData<ProfileUser> = Transformations.map(RetrofitLiveData(apiService.signUpGuest(email, token), error)) { it.result }

    fun signUpGuest(email: String, password: String, error: MutableLiveData<Throwable>):
            LiveData<ProfileUser> = Transformations.map(RetrofitLiveData(apiService.setUpProfileGuest(email, password), error)) { it.result }
}
