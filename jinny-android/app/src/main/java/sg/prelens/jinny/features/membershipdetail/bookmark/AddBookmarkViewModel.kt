package sg.prelens.jinny.features.membershipdetail.bookmark

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.repositories.AddBookmarkRespository
import sg.prelens.jinny.utils.AbsentLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class AddBookmarkViewModel(repository: AddBookmarkRespository) : ViewModel() {
    private val addBookmarkRequest = MutableLiveData<Int>()
    val addBookmarkLiveData: LiveData<AddBookmarkResonse>
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    fun setBookMarkID(id: Int) {
        this.addBookmarkRequest.value = id
    }

    init {
        addBookmarkLiveData = Transformations.switchMap<Int, AddBookmarkResonse>(addBookmarkRequest,
                {
                    if (it == null) {
                        AbsentLiveData.create()
                    } else
                        repository.addBookmark(it, errorLiveData)
                })
    }
}