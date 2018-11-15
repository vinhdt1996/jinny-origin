package sg.prelens.jinny.utils

import android.arch.lifecycle.LiveData
import sg.prelens.jinny.PagingRequestHelper
import sg.prelens.jinny.repositories.NetworkState

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/11/18<br>.
 */

private fun getErrorMessage(report: PagingRequestHelper.StatusReport): String {
    return PagingRequestHelper.RequestType.values().mapNotNull {
        report.getErrorFor(it)?.message
    }.first()
}

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = android.arch.lifecycle.MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(
                    NetworkState.error(getErrorMessage(report)))
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}