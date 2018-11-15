package sg.prelens.jinny.features.settings

import android.content.Context
import android.content.SharedPreferences

class SettingPrefs(context: Context) {
    companion object {
        private const val PREFS_FILENAME = "sg.prelens.jinny.prefs"
        private const val PUSH_NOTIFICATION = "push_notification"
        private const val DISCOUNT_ALERT = "discount_alert"
        private const val EXPIRY_NOTIFICATION = "expiry_notification"
        private const val DAY_TO_REMIND = "day_to_remind"
        private const val FIRST_TIME_OPEN = "first_time_open_app"
        private const val OLD_FCM_TOKEN = "old_FCM_Token"
        private const val NOTIFICATION_LIST = "notification_list"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var pushNotification: Boolean
        get() = prefs.getBoolean(PUSH_NOTIFICATION, false)
        set(value) = prefs.edit().putBoolean(PUSH_NOTIFICATION, value).apply()

    var storeDiscountAlert: Boolean
        get() = prefs.getBoolean(DISCOUNT_ALERT, false)
        set(value) = prefs.edit().putBoolean(DISCOUNT_ALERT, value).apply()

    var voucherExpiryNotification: Boolean
        get() = prefs.getBoolean(EXPIRY_NOTIFICATION, false)
        set(value) = prefs.edit().putBoolean(EXPIRY_NOTIFICATION, value).apply()

    var dayToRemindBeforeExpire: Int
        get() = prefs.getInt(DAY_TO_REMIND, 7)
        set(value) = prefs.edit().putInt(DAY_TO_REMIND, value).apply()

    var firstTimeOpenApp: Boolean
        get() = prefs.getBoolean(FIRST_TIME_OPEN, true)
        set(value) = prefs.edit().putBoolean(FIRST_TIME_OPEN, value).apply()

    var oldFCMToken: String
        get() = prefs.getString(OLD_FCM_TOKEN, "")
        set(value) = prefs.edit().putString(OLD_FCM_TOKEN, value).apply()

    fun resetSetting() {
        pushNotification = false
        storeDiscountAlert = false
        voucherExpiryNotification = false
        dayToRemindBeforeExpire = 7
        oldFCMToken = ""
        notification_id_list = setOf()
    }

    var notification_id_list: Set<String>
        get() = prefs.getStringSet(NOTIFICATION_LIST, setOf())
        set(value) = prefs.edit().putStringSet(OLD_FCM_TOKEN, value).apply()


    fun openSetting(){
        pushNotification = true
        storeDiscountAlert = true
        voucherExpiryNotification = true
        dayToRemindBeforeExpire = 7
    }
}