package sg.prelens.jinny.features.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import sg.prelens.jinny.models.LogoutResponse
import sg.prelens.jinny.repositories.LogoutRespository

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/16/18<br>.
 */
class LogoutViewModel(repository: LogoutRespository) : ViewModel() {
    val logoutAccountLiveData: LiveData<LogoutResponse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    init {
        logoutAccountLiveData = repository.logoutAccount(
                FirebaseInstanceId.getInstance().token ?: "",
                errorLiveData
        )
    }
}
