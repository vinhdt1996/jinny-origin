package sg.prelens.jinny.features.auth.change

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.ChangePasswordResponse
import sg.prelens.jinny.repositories.ChangePasswordRespository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/18/18<br>.
 */
class ChangePasswordViewModel(repository: ChangePasswordRespository) : ViewModel() {
    private val changePasswordRequest = MutableLiveData<Pair<String, String>>()
    val changePasswordLiveData: LiveData<ChangePasswordResponse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData<Throwable>()
    fun setChangePassword(currentPassword: String, newPassWord: String) {
        this.changePasswordRequest.value = Pair(currentPassword, newPassWord)
    }

    init {
        changePasswordLiveData = Transformations.switchMap<Pair<String, String>, ChangePasswordResponse>(changePasswordRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.changePassword(it.first, it.second, errorLiveData)
                })
    }
}