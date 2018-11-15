package sg.prelens.jinny.service.tracking

import android.provider.Settings
import android.util.Log
import androidx.work.Worker
import com.example.roomdbAnalytics.dao.RoomDB
import com.google.gson.Gson
import sg.prelens.jinny.api.ApiGenerator
import sg.prelens.jinny.exts.setUp
import sg.prelens.jinny.exts.toTrackingModel
import sg.prelens.jinny.utils.SharePreference

class TrackingListEventWorker : Worker() {
    override fun doWork(): Result {
        val roomDB = RoomDB.newInstance(applicationContext)
        val data = roomDB?.myDao()?.queryDefaultAnalytics()
        val gson = Gson()
        val dataStr = gson.toJson(data)
        if (dataStr != null) {
            val api = ApiGenerator.createTracking()
            val response = api.trackListEvent(dataStr).execute()
            if (response?.isSuccessful == false) {
                return Result.FAILURE
            }
            return Result.SUCCESS
        }
        return Result.FAILURE
    }
}