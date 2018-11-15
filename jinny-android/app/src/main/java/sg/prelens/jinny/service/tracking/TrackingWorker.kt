package sg.prelens.jinny.service.tracking

import androidx.work.Worker
import sg.prelens.jinny.api.ApiGenerator
import sg.prelens.jinny.exts.toTrackingModel

class TrackingWorker() : Worker() {

    override fun doWork(): Result {
        val data = inputData.toTrackingModel()
        val api = ApiGenerator.createTracking()
        val response = api?.trackEvent(data)?.execute()
        if (response?.isSuccessful == false)
            return Result.FAILURE
        return Result.SUCCESS
    }

}