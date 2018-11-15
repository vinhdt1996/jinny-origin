package sg.prelens.jinny.service.tracking

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Room
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.setUp
import sg.prelens.jinny.exts.toData
import sg.vinova.trackingtool.model.BaseTrackingModel
import sg.vinova.trackingtool.model.EventType
import kotlin.jvm.java
import android.net.ConnectivityManager.TYPE_WIFI
import android.provider.Settings
import androidx.work.*
import org.jetbrains.anko.doAsyncResult
import sg.prelens.jinny.utils.SharePreference


class TrackingHelper {
    companion object {

        fun sendEventUtil(eventType: Int, eventName: String, eventDescription: String, context: Context) {
            val event = BaseTrackingModel(eventType, eventName, eventDescription)
                    .also { it.setUp(context) }
            val worker = OneTimeWorkRequest.Builder(TrackingWorker::class.java)
                    .setInputData(event.toData())
            val work = worker.build()
            WorkManager.getInstance().beginUniqueWork(AnalyticConst.TRACKING_WORK, ExistingWorkPolicy.REPLACE, work)
                    .enqueue()
        }

        /**
         * @param eventType: {@link EventType}
         * @param eventName: App Event
         * @param eventDescription: "Merchant Name: <Merchant name>, Code: <membership code>, Membership ID: <membership id>."
         */
        fun sendEvent(eventType: Int, eventName: String, eventDescription: String, context: Context): LiveData<WorkStatus>? {
            val event = BaseTrackingModel(eventType, eventName, eventDescription)
                    .also { it.setUp(context) }
            val worker = OneTimeWorkRequest.Builder(TrackingWorker::class.java)
                    .setInputData(event.toData())
            val work = worker.build()
            WorkManager.getInstance().beginUniqueWork(AnalyticConst.TRACKING_WORK, ExistingWorkPolicy.REPLACE, work)
                    .enqueue()
            return WorkManager.getInstance().getStatusById(work.getId())
        }

//        fun sendEventList(eventList : List<DefaultAnalytics>?, context: Context): LiveData<WorkStatus>? {
//            val worker = OneTimeWorkRequest.Builder(TrackingWorker::class.java)
//                    .setInputData(eventList)
//            val work = worker.build()
//            WorkManager.getInstance().beginUniqueWork(AnalyticConst.TRACKING_WORK, ExistingWorkPolicy.REPLACE, work)
//                    .enqueue()
//            return WorkManager.getInstance().getStatusById(work.getId())
//        }

        fun sendListEvent() : LiveData<WorkStatus>? {
            val worker = OneTimeWorkRequest.Builder(TrackingListEventWorker::class.java)
            val work = worker.build()
            WorkManager.getInstance().beginUniqueWork(AnalyticConst.TRACKING_WORK, ExistingWorkPolicy.REPLACE, work).enqueue()
            return WorkManager.getInstance().getStatusById(work.getId())
        }

        fun setDB(default: DefaultAnalytics?, roomDB: RoomDB, context: Context) {
            doAsyncResult {
                val profile = SharePreference.getInstance().getGuestAccount()
                default?.user_id = profile?.id
                default?.device_id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                roomDB.myDao().insertDefaultAnalytics(default)
            }
        }

        fun deleteDB(roomDB: RoomDB) {
            doAsyncResult {
                roomDB.myDao().deleteDefaultAnalytics()
            }
        }

        fun hasConnection(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (wifiNetwork != null && wifiNetwork.isConnected) {
                return true
            }
            val mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            if (mobileNetwork != null && mobileNetwork.isConnected) {
                return true
            }
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected) {
                return true
            }
            return false
        }
    }
}