package sg.prelens.jinny.api

import android.util.Base64
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sg.prelens.jinny.BuildConfig
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.api.TrackingApi
import sg.vinova.tvapp.util.LiveDataCallAdapterFactory
import tech.linjiang.pandora.Pandora
import java.util.concurrent.TimeUnit

/**
 * Author : BIMBIM<br>.
 * Create Date : 3/12/18<br>.
 */
class ApiGenerator {
    companion object {
        private const val BASE_URL = BuildConfig.SERVER_URL
        private val httpClient = OkHttpClient.Builder()
        fun create(): ApiLink = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): ApiLink {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BODY
            //httpClient.addInterceptor(logger)
            httpClient.readTimeout(15, TimeUnit.SECONDS)
            httpClient.connectTimeout(15, TimeUnit.SECONDS)
            httpClient.writeTimeout(15, TimeUnit.SECONDS)
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                // Request customization: add request headers
                val token = JinnyApplication.instance.currentUser?.token
                val tokenGuest = SharePreference.getInstance().getGuestAccount()?.token
                var requestBuilder: Request.Builder? = null
                if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
                    if (tokenGuest != null) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", tokenGuest)
                    }
                } else {
                    if (!token.isNullOrEmpty()) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", token) // <-- this is the important line
                    } else {
                        requestBuilder = original.newBuilder()
                    }
                }
                val request = requestBuilder?.build()
                chain.proceed(request)
            }
            httpClient.addNetworkInterceptor { chain ->
                val request = chain.request()
                /*request = request.newBuilder()
                        .addHeader("AUTHENTICATION", "")
                        .addHeader("AUTHORIZED", "")
                        .build()*/
                val response = chain.proceed(request)
//                val token = JinnyApplication.instance.currentUser?.token
//                if(!token.isNullOrEmpty()){
//                    response.newBuilder()
//                            .addHeader("Jinny-Http-Token", token!!)
//                            .body(ProgressResponseBody(response.body()!!)).build()
//                } else {
                response.newBuilder()
                        .body(ProgressResponseBody(response.body()!!)).build()
//                }
                response
            }.interceptors().add(logger)
            if (BuildConfig.DEBUG) {
                httpClient.addNetworkInterceptor(StethoInterceptor())
                        .addInterceptor(Pandora.get().getInterceptor())


            }
            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .build()
                    .create(ApiLink::class.java)
        }
        fun createUpload(): ApiLink {
            val httpUrl = HttpUrl.parse(BASE_URL)
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BODY
            //httpClient.addInterceptor(logger)
            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            httpClient.writeTimeout(10, TimeUnit.SECONDS)
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                // Request customization: add request headers
                val token = JinnyApplication.instance.currentUser?.token
                val tokenGuest = SharePreference.getInstance().getGuestAccount()?.token
                var requestBuilder: Request.Builder? = null
                if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
                    if (tokenGuest != null) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", tokenGuest)
                    }
                } else {
                    if (!token.isNullOrEmpty()) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", token) // <-- this is the important line
                    } else {
                        requestBuilder = original.newBuilder()
                    }
                }
                val request = requestBuilder?.build()
                chain.proceed(request)
            }
            httpClient.addNetworkInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                response.newBuilder()
                        .body(ProgressResponseBody(response.body()!!)).build()
                response
            }.interceptors().add(logger)
            if (BuildConfig.DEBUG) {
                httpClient.addNetworkInterceptor(StethoInterceptor())
                        .addInterceptor(Pandora.get().getInterceptor())
            }
            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .build()
                    .create(ApiLink::class.java)
        }
        fun createTracking(): TrackingApi {
            val httpUrl = HttpUrl.parse(BASE_URL)
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BODY
            //httpClient.addInterceptor(logger)
            httpClient.connectTimeout(10, TimeUnit.SECONDS)
            httpClient.readTimeout(10, TimeUnit.SECONDS)
            httpClient.writeTimeout(10, TimeUnit.SECONDS)
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                // Request customization: add request headers
                val token = JinnyApplication.instance.currentUser?.token
                val tokenGuest = SharePreference.getInstance().getGuestAccount()?.token
                var requestBuilder: Request.Builder? = null
                if (SharePreference.getInstance().getGuestAccount()?.guest == true) {
                    if (tokenGuest != null) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", tokenGuest)
                    }
                } else {
                    if (!token.isNullOrEmpty()) {
                        requestBuilder = original.newBuilder()
                                .header("Jinny-Http-Token", token) // <-- this is the important line
                    } else {
                        requestBuilder = original.newBuilder()
                    }
                }
                val request = requestBuilder?.build()
                chain.proceed(request)
            }
            httpClient.addNetworkInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                response.newBuilder()
                        .body(ProgressResponseBody(response.body()!!)).build()
                response
            }.interceptors().add(logger)
            if (BuildConfig.DEBUG) {
                httpClient.addNetworkInterceptor(StethoInterceptor())
                        .addInterceptor(Pandora.get().getInterceptor())
            }
            return Retrofit.Builder()
                    .baseUrl(httpUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(LiveDataCallAdapterFactory())
                    .build()
                    .create(TrackingApi::class.java)
        }
    }
}