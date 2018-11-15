package sg.vinova.tvapp.util

import android.arch.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.Retrofit
import sg.prelens.jinny.utils.ApiResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Created by tommy on 3/19/18.
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        if (CallAdapter.Factory.getRawType(returnType ?: return null) != LiveData::class.java) {
            return null
        }
        val observableType = CallAdapter.Factory.getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = CallAdapter.Factory.getRawType(observableType)
        if (rawObservableType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Any>(bodyType)
    }

}