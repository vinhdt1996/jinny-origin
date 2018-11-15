package sg.prelens.jinny.exts

import android.content.Context
import android.provider.Settings
import android.util.EventLog
import androidx.work.Data
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.BaseTrackingModel
import sg.vinova.trackingtool.model.EventType
import java.util.*


fun BaseTrackingModel.setUp(context: Context){
    this.deviceId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    val profile = SharePreference.getInstance().getGuestAccount()
    this.userId  = profile?.id ?: ""
    this.timeStamp = Calendar.getInstance().timeInMillis.toString()
}

fun BaseTrackingModel.toData() : Data =
    Data.Builder().putString("device_Id", deviceId)
            .putString("userId", userId)
            .putString("timeStamp", timeStamp)
            .putInt("eventType", eventType)
            .putString("eventName", eventName)
            .putString("eventDescription", eventDescription)
            .build()

fun Data.toTrackingModel(): BaseTrackingModel=
    BaseTrackingModel(
            deviceId = getString("device_Id") ?: "",
            timeStamp = getString("timeStamp")?: "",
            userId = getString("userId")?:"",
            eventType = getInt("eventType", 0),
            eventName = getString("eventName")?: "",
            eventDescription = getString("eventDescription")
    )
