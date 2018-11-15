package sg.prelens.jinny.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log

import com.google.firebase.messaging.RemoteMessage
import sg.prelens.jinny.R
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.settings.SettingPrefs
import java.util.*
import android.graphics.BitmapFactory
import com.google.firebase.messaging.CustomFirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class JFirebaseMessagingService : CustomFirebaseMessagingService() {
    companion object {
        const val NOTIFICATION_INFO_REQUEST = 0xFC0002
        val TAG = JFirebaseMessagingService::class.java.simpleName!!
    }

    private lateinit var setting: SettingPrefs

    override fun onCreate() {
        super.onCreate()
        setting = SettingPrefs(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
//        super.onMessageReceived(remoteMessage)
        if (remoteMessage == null) return

        val setNotificationId = setting.notification_id_list

        if (remoteMessage.messageId.isNullOrEmpty().not())
            for (notificationId in setNotificationId) {
                if (notificationId == remoteMessage.messageId) {
                    Log.d(TAG, "onMessageReceived: $notificationId id are already handled")
                    return
                } else {
                    Log.d(TAG, "onMessageReceived:  save this $notificationId id ")
                    setting.notification_id_list = setNotificationId
                            .plus(remoteMessage.messageId ?: "")
                }
            }


        Log.d(TAG, "Received message from ${remoteMessage.from}")
        // check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }


        // check if message contains a notification payload
        val body = remoteMessage.data
        Log.d(TAG, "Message notification body: $body")

        body?.let {
            //if (SettingPrefs(this).storeDiscountAlert)
            sendNotification(it)
        }

    }

    /**
     * Creates and shows a simple notification containing the received FCM message.
     * @param messageBody FCM message body received
     */
    private fun sendNotification(messageBody: MutableMap<String, String>) {
        val random = Random()
        val randomNum = random.nextInt(9999 - 1000) + 1000

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("fromNotifcation", true)
        val pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_INFO_REQUEST, intent, PendingIntent.FLAG_ONE_SHOT)
        intent.putExtra("fromNotification", true)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)

        val charTitle = messageBody["title"] ?: ""
        val charBody = messageBody["message"] ?: ""

        TrackingHelper.sendEventUtil(EventType.APP_EVENT, AnalyticConst.notification_foreground, "", this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanelName = "Jinny" as CharSequence
            val chanelId = "Jinny"
            val notiChanel = NotificationChannel(chanelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
            notiChanel.description = "Notification from Jinny"
            notiChanel.lightColor = ContextCompat.getColor(applicationContext, R.color.pending)
            notificationManager.createNotificationChannel(notiChanel)

            val notificationBuilder = NotificationCompat.Builder(this, chanelId)
                    .setSmallIcon(R.drawable.ic_stat_noti_jiny)
                    .setColor(ContextCompat.getColor(this, R.color.star_color))
                    .setLargeIcon(icon)
                    .setContentTitle(charTitle)
                    .setContentText(charBody)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(charBody))
            notificationManager.notify(randomNum, notificationBuilder.build())
        } else {
            val notificationBuilder = NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_noti_jiny)
                    .setColor(ContextCompat.getColor(this, R.color.star_color))
                    .setLargeIcon(icon)
                    .setContentTitle(charTitle)
                    .setContentText(charBody)
                    .setCategory(Notification.CATEGORY_PROMO)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle()
                            .bigText(charBody))
            notificationManager.notify(randomNum, notificationBuilder.build())
        }
    }

    override fun onMessageSent(p0: String?) {
        super.onMessageSent(p0)
    }
}
