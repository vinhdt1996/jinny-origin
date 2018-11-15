package sg.prelens.jinny.features.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.repositories.ShareDealRepository
import sg.prelens.jinny.repositories.ShareDealResponse
import sg.prelens.jinny.utils.AbsentLiveData

class MainActivityViewModel(repository: ShareDealRepository) : ViewModel() {

    val error = MutableLiveData<Throwable>()
    val request = MutableLiveData<String>()
    val result: LiveData<ShareDealResponse>

    fun requestShareDeal(id: String?) {
        request.value = id
    }

    init {
        result = Transformations.switchMap(request, {
            if(it == null) {
                AbsentLiveData.create()
            } else
                repository.addShareDeal(it,error)
        })
    }

}