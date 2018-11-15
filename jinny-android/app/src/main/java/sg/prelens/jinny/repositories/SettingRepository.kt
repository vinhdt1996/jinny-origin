package sg.prelens.jinny.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import sg.prelens.jinny.api.ApiLink
import sg.prelens.jinny.models.SettingNotificationModel
import sg.prelens.jinny.models.SettingNotificationResponse
import sg.prelens.jinny.utils.RetrofitLiveData
import sg.vinova.tvapp.util.LiveDataCallAdapter
import java.nio.channels.MulticastChannel

class SettingRepository(val apiService: ApiLink) {
    val error = MutableLiveData<Throwable>()
    fun putSetting(push_notification: Boolean, store_discount_alert: Boolean, voucher_expiry: Boolean, days_to_remind: Int?): LiveData<SettingNotificationResponse> =
            RetrofitLiveData(apiService.putNotificationSetting(push_notification, store_discount_alert, voucher_expiry, days_to_remind
                    ?: 7), error)

    fun getSetting(): LiveData<SettingNotificationResponse> = RetrofitLiveData(apiService.getNotificationSetting(), error)
}