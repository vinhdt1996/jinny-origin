package sg.vinova.trackingtool.api

import com.example.roomdbAnalytics.model.DefaultAnalytics
import retrofit2.Call
import retrofit2.http.*
import sg.vinova.trackingtool.model.BaseTrackingModel
import sg.vinova.trackingtool.model.TrackingResponse

interface TrackingApi {
    @POST("/api/v1/analytic_events")
    @Headers("Content-Type: application/json")
    fun trackEvent(@Body trackingData: BaseTrackingModel): Call<TrackingResponse>

    @POST("/api/v1/analytic_events")
    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    fun trackEvent(deviceId: String, userId: String, eventType: Int, eventName: String): Call<TrackingResponse>

    @POST("api/v1/analytic_events/offline_records")
    @FormUrlEncoded
    fun trackListEvent(@Field("analytic_events") analytic_events: String?): Call<TrackingResponse>
}