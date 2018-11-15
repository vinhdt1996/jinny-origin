package sg.prelens.jinny.service

import android.app.*
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Messenger
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.MESSENGER_INTENT_KEY
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.features.promotion.PromotionViewModel
import sg.prelens.jinny.features.promotiondetail.PromotionDetailActivity
import sg.prelens.jinny.features.settings.SettingPrefs
import sg.prelens.jinny.models.PromotionList
import sg.prelens.jinny.utils.JobUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ExpiringService : JobService(), LifecycleOwner {

    private lateinit var lifeCycleRegistry: LifecycleRegistry
    private var activityMessenger: Messenger? = null
    private lateinit var model: PromotionViewModel
    private lateinit var prefSetting: SettingPrefs
    companion object {
        val TAG = ExpiringService::class.java.simpleName!!
    }

    override fun onCreate() {
        super.onCreate()
        lifeCycleRegistry = LifecycleRegistry(this)
        lifeCycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        activityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY)
        return Service.START_NOT_STICKY
    }

    override fun onStartJob(params: JobParameters): Boolean {
        Log.i(TAG, "on start job: ${params.jobId}")
        lifeCycleRegistry.markState(Lifecycle.State.STARTED)
        prefSetting = SettingPrefs(this)
        val remindDay = prefSetting.dayToRemindBeforeExpire
        if(!prefSetting.pushNotification || !prefSetting.voucherExpiryNotification){
            jobFinished(params, false)
            return false
        }
        model = getViewModel()
        var get = 0
        model.promotions.observe(this, Observer<PagedList<PromotionList>> {
            it?.forEach {
                Log.d(TAG, it?.toString())
                if (calculateDifference(b = getDateFromString(it.expires_at)) == remindDay) {
                    sendNotification(StringBuilder().append("Voucher ")
                            .append(it.description)
                            .append(" from ")
                            .append(it.merchant.name)
                            .append(" will expires at ")
                            .append(it?.expires_at_in_words).toString(), it)
                    get++
                }
            }
            Log.d(TAG+" remindDay", remindDay.toString())
            Log.d(TAG+ " total", it?.size.toString())
            Log.d(TAG+" get", get.toString())
            Handler().postDelayed({
                reScheduleJob()
                jobFinished(params, false)
            }, TimeUnit.MINUTES.toMillis(1))
        })
        model.filter()
        return true
    }

    override fun getLifecycle(): Lifecycle {
        return lifeCycleRegistry
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.i(TAG, "on stop job: ${params.jobId}")
        return false
    }

    private fun reScheduleJob() {
        if (!SettingPrefs(this).pushNotification || !SettingPrefs(this).voucherExpiryNotification) return
        Log.d(TAG, "ReScheduling job")
        (getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).schedule(
                JobUtil.getJobBuilder(
                        context = this@ExpiringService,
                        jobId = 51)
                        .build())
    }

    private fun sendNotification(messageBody: String, voucher: PromotionList) {
        if (!SettingPrefs(this).pushNotification || !SettingPrefs(this).voucherExpiryNotification) return
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val intent = Intent(this, PromotionDetailActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("voucherName", voucher.merchant.name)
        intent.putExtra("voucherId", voucher.id)
        intent.putExtra("usersVoucherId", voucher.users_voucher_id)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        val resultPendingIntent = stackBuilder.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val glide = Glide.with(this)
        var bmp: Bitmap?

        glide.load(voucher.merchant.logo?.url?.thumb)
                .asBitmap()
                .into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                        Log.d(TAG, "Loaded Logo")
                        bmp = resource
                        val chanelName = "Jinny" as CharSequence
                        val chanelId = "Jinny"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val noticeChanel = NotificationChannel(chanelId, chanelName, NotificationManager.IMPORTANCE_HIGH)
                            noticeChanel.description = "Notification from Jinny"
                            noticeChanel.lightColor = ContextCompat.getColor(applicationContext, R.color.crimson)
                            notificationManager.createNotificationChannel(noticeChanel)
                        }
                        val notificationBuilder = NotificationCompat.Builder(this@ExpiringService, chanelId)
                                .setSmallIcon(R.drawable.ic_stat_noti_jiny)
                                .setColor(ContextCompat.getColor(applicationContext, R.color.crimson))
                                .setContentTitle("Voucher " + voucher.merchant.name)
                                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody).setBigContentTitle("Voucher " + voucher.merchant.name)
                                        .setSummaryText("Voucher " + voucher.merchant.name))
                                .setCategory(Notification.CATEGORY_PROMO)
                                .setAutoCancel(true)
                                .setColorized(true)
                                .setLargeIcon(bmp)
                                .setSound(defaultSoundUri)
                                .setContentIntent(resultPendingIntent)
                        Log.d(TAG, "Notify")
                        notificationManager.notify(m, notificationBuilder.build())
                    }
                })
    }

    private fun getViewModel(): PromotionViewModel {
        return PromotionViewModel(
                ServiceLocator.instance(this)
                        .getPromotionRepository(), null, null)
    }

    private fun getDateFromString(date: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        return sdf.parse(date) as Date
    }

    private fun calculateDifference(a: Date = Date(), b: Date): Int {
        var tempDifference: Int
        var difference = 0
        Log.d("calculateDifference a ", a.toString())
        Log.d("calculateDifference b ", b.toString())
        val earlier = Calendar.getInstance()
        val later = Calendar.getInstance()

        if (a < b) {
            earlier.time = a
            later.time = b
        } else {
            earlier.time = b
            later.time = a
        }

        while (earlier[Calendar.YEAR] != later[Calendar.YEAR]) {
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR))
            difference += tempDifference
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference)
        }

        if (earlier[Calendar.DAY_OF_YEAR] != later[Calendar.DAY_OF_YEAR]) {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR)
            difference += tempDifference
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference)
        }

        return difference
    }
}


