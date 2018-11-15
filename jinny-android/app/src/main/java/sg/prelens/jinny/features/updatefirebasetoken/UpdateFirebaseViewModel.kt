package sg.prelens.jinny.features.updatefirebasetoken

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.ChangeFirebaseResponse
import sg.prelens.jinny.repositories.AuthRepository
import sg.prelens.jinny.utils.AbsentLiveData

class UpdateFirebaseViewModel(repository: AuthRepository): ViewModel(){
    private val changeTokenRequest = MutableLiveData<Pair<String, String>?>()

    val tokenLiveData: LiveData<ChangeFirebaseResponse>

    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun setToken(oldToken: String, newToken: String) {
        this.changeTokenRequest.value = Pair(oldToken, newToken)
    }

    init {
        tokenLiveData = Transformations.switchMap<Pair<String, String>, ChangeFirebaseResponse>(changeTokenRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.changeFirebaseToken(it.first, it.second, errorLiveData)
            }
        })
    }
}