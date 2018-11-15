package sg.prelens.jinny.utils

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sg.prelens.jinny.exceptions.NetworkErrorException
import sg.prelens.jinny.exts.log
import sg.prelens.jinny.models.BaseResponse

open class RetrofitLiveData<T : BaseResponse>(private val call: Call<T>,
                                              private val error: MutableLiveData<Throwable>,
                                              private var retry: Retry? = null, private var refresh: Refresh? = null) : LiveData<T>(), Callback<T> {
    override fun onActive() {
        if (!call.isCanceled && !call.isExecuted) {
            call.enqueue(this)
        }
        if (refresh != null) {
            refresh?.refreshFunc =
                    {
                        call?.clone()?.enqueue(this@RetrofitLiveData)
                    }
        }
    }

    override fun onFailure(call: Call<T>?, t: Throwable?) {
        error.value = t ?: NetworkErrorException.DEFAULT_ERROR
        retry?.retryFunc = {
            call?.clone()?.enqueue(this@RetrofitLiveData)
        }
    }

    override fun onResponse(call: Call<T>?, response: Response<T>?) {
        if ((response?.isSuccessful == true) && (response.body()?.isSuccess() == true)) {
            value = response.body()
            error.value = null
            retry?.retryFunc = null

        } else {
            error.value = NetworkErrorException.newWith(response?.body())
            retry?.retryFunc = {
                call?.clone()?.enqueue(this@RetrofitLiveData)
            }
        }
    }

    fun cancel() = if (!call.isCanceled) call.cancel() else Unit
    class Retry(var retryFunc: (() -> Any?)? = null)
    class Refresh(var refreshFunc: (() -> Any?)? = null)
}