package sg.vinova.tvapp.util

import android.arch.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import sg.prelens.jinny.utils.ApiResponse
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created by tommy on 3/19/18.
 */
/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<R, LiveData<ApiResponse<R>>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            internal var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            postValue(ApiResponse(response = response, retry = { call.clone().enqueue(this) }))
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            postValue(ApiResponse(error = throwable, retry = { call.clone().enqueue(this) }))
                        }
                    })
                }
            }
        }
    }
}