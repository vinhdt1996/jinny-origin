package sg.prelens.jinny.features.auth.forgot

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.ForgotPasswordResponse
import sg.prelens.jinny.models.User
import sg.prelens.jinny.repositories.ForgotPasswordRespository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/15/18<br>.
 */
class ForgotPasswordViewModel(repository: ForgotPasswordRespository) : ViewModel() {
    private val forgotPasswordRequest = MutableLiveData<String>()
    val forgotPasswordLiveData: LiveData<ForgotPasswordResponse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun setForgotPassword(email: String) {
        this.forgotPasswordRequest.value = email
    }

    init {
        forgotPasswordLiveData = Transformations.switchMap<String, ForgotPasswordResponse>(forgotPasswordRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.forgotPassword(it, errorLiveData)
                })
    }
}