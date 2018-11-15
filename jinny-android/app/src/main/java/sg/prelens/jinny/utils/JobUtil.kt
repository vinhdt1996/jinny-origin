package sg.prelens.jinny.utils

import android.app.job.JobInfo
import android.app.job.JobInfo.NETWORK_TYPE_ANY
import android.content.ComponentName
import android.content.Context
import org.jetbrains.annotations.NotNull
import sg.prelens.jinny.exts.log
import sg.prelens.jinny.service.ExpiringService
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log


class JobUtil {
    companion object {

        fun getJobBuilder(@NotNull context: Context, timeToSchedule: Int = 7, jobId: Int = 50): JobInfo.Builder {
            val serviceComponent = ComponentName(context, ExpiringService::class.java)
            val builder = JobInfo.Builder(jobId, serviceComponent)

            builder.setMinimumLatency(getMilisFromNowToTime(timeToSchedule)) // wait at least
            builder.setRequiredNetworkType(NETWORK_TYPE_ANY)
            builder.setOverrideDeadline(TimeUnit.DAYS.toMillis(1)) // maximum delay'
            builder.setPersisted(true)
            builder.setRequiresDeviceIdle(false)
            builder.setRequiresCharging(false)

            return builder
        }

        private fun getMilisFromNowToTime(timeToConvert: Int = 6): Long {
            val nowDate = Calendar.getInstance()
            val futureDate = Calendar.getInstance()

            if (nowDate.get(Calendar.HOUR_OF_DAY) >= 12) {
                futureDate.add(Calendar.DAY_OF_YEAR, 1)
            }
            futureDate.set(Calendar.HOUR_OF_DAY, 12)

            log("JobUtil: milis ${futureDate.timeInMillis - nowDate.timeInMillis} ")
            return futureDate.timeInMillis - nowDate.timeInMillis
        }
    }
}
