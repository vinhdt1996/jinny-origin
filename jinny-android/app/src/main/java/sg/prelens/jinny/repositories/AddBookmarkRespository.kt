package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.R.string.email
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.AddBookmarkResonse
import sg.prelens.jinny.utils.RetrofitLiveData

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/22/18<br>.
 */
class AddBookmarkRespository(val apiService: ApiLink) {
    fun addBookmark(id: Int, error: MutableLiveData<Throwable>): LiveData<AddBookmarkResonse> = RetrofitLiveData(apiService.addBookmark(id), error)
}