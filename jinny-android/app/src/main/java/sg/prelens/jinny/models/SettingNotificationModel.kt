package sg.prelens.jinny.models

data class SettingNotificationModel(
        val push_notification: Boolean,
        val store_discount_alert: Boolean,
        val voucher_expiry: Boolean,
        val days_to_remind: Int
)