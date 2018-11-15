package sg.prelens.jinny.features.settings

import android.arch.lifecycle.*
import sg.prelens.jinny.models.SettingNotificationModel
import sg.prelens.jinny.models.SettingNotificationResponse
import sg.prelens.jinny.repositories.SettingRepository
import sg.prelens.jinny.utils.AbsentLiveData
import sg.prelens.jinny.utils.RetrofitLiveData

class SettingViewModel(val repository: SettingRepository) : ViewModel() {
    //    val is_push_noti = MutableLiveData<Boolean>()
//    val is_store_discount_alert = MutableLiveData<Boolean>()
//    val is_voucher_expiry = MutableLiveData<Boolean>()
//    val days_to_remind = MutableLiveData<Int>()
    val error: MutableLiveData<Throwable>
    val settingRequest = MutableLiveData<SettingNotificationModel>()
    val putSetting: LiveData<SettingNotificationResponse>
    val settingResponse: LiveData<SettingNotificationResponse> = repository.getSetting()

//    public fun setPushNoti(boolean: Boolean) {
//        is_push_noti.value = boolean
//    }
//
//    public fun setStoreDiscount(boolean: Boolean) {
//        is_store_discount_alert.value = boolean
//    }
//
//    public fun setVoucherExpire(boolean: Boolean) {
//        is_voucher_expiry.value = boolean
//    }
//
//    public fun setDayToRemind(days: Int) {
//        days_to_remind.value = days
//    }

    fun setSetting(setting: SettingNotificationModel) {
        settingRequest.value = setting
    }

    init {
//        settingRequest.addSource(is_push_noti) {
//            updateSetting()
//        }
        putSetting = Transformations.switchMap(settingRequest, {
            if (it == null) {
                AbsentLiveData.create()
            } else {
                repository.putSetting(it.push_notification, it.store_discount_alert, it.voucher_expiry, it.days_to_remind)
            }
        })
        error = repository.error
    }

//    private fun updateSetting() {
//
//    }
}