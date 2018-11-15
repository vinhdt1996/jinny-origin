package sg.prelens.jinny.utils

import android.arch.lifecycle.LiveData

/**
 * Created by tommy on 3/13/18.
 */
class AbsentLiveData<T> private constructor() : LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> = AbsentLiveData()
    }
}