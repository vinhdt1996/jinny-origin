package sg.prelens.jinny.service

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.constant.MESSENGER_INTENT_KEY
import sg.prelens.jinny.constant.MSG_STOP
import sg.prelens.jinny.constant.MSG_UPDATE_VOUCHER
import sg.prelens.jinny.features.promotion.PromotionViewModel
import sg.prelens.jinny.models.PromotionList
import java.util.concurrent.TimeUnit

class UpdateBadgeService : JobService(), LifecycleOwner {
    private lateinit var lifeCycleRegistry: LifecycleRegistry
    private var activityMessenger: Messenger? = null
    private lateinit var model: PromotionViewModel

    companion object {
        val TAG = UpdateBadgeService::class.java.simpleName!!
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

    override fun getLifecycle(): Lifecycle {
        return lifeCycleRegistry
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return JinnyApplication.isActive
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        lifeCycleRegistry.markState(Lifecycle.State.STARTED)
        Log.d(TAG, "Start Service")
        Log.d(TAG, Thread.currentThread().name ?: "unknown")
        model = getViewModel()
        model.promotions.observe(this, Observer<PagedList<PromotionList>> {
            Log.d(TAG, "Receive Promotion")
            val newItem = it?.filter { !it.is_read }

            sendMessage(MSG_UPDATE_VOUCHER, newItem?.size ?: 0)
            Log.d(TAG, it?.size.toString())
//            Handler().postDelayed({
//              //  sendMessage(MSG_STOP, params?.jobId)
//                jobFinished(params, true)
//            }, TimeUnit.SECONDS.toMillis(60))
        })
        model.filter()
        return true
    }

    private fun sendMessage(messageID: Int, params: Any?) {
        if (activityMessenger == null) {
            Log.d(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }

        val message = Message.obtain()
        message.run {
            what = messageID
            obj = params
        }
        try {
            activityMessenger?.send(message)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }
    }

    private fun getViewModel(): PromotionViewModel {
        return PromotionViewModel(
                ServiceLocator.instance(this)
                        .getPromotionRepository(), null, null)
    }
}
