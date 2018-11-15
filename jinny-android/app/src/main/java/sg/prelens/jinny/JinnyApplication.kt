package sg.prelens.jinny

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import com.crashlytics.android.Crashlytics
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import io.fabric.sdk.android.Fabric
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.getCurrentUser
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.ActivityLifeCycleAdapter
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.EventType
import tech.linjiang.pandora.Pandora


class JinnyApplication : Application(), LifecycleObserver {
    var currentUser: ProfileUser? = null
    private var roomDB: RoomDB? = null
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        roomDB = RoomDB.newInstance(this)
        if (!BuildConfig.DEBUG) Fabric.with(this, Crashlytics())
        instance = this
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
            Pandora.get().open()
        }
        currentUser = getCurrentUser()
        SharePreference.getInstance().getContext(applicationContext).getSharedPreferences()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        registerActivityLifecycleCallbacks(object : ActivityLifeCycleAdapter() {})

    }

    fun reloadCurUser() {
        currentUser = getCurrentUser()
    }

    companion object {
        lateinit var instance: JinnyApplication
            private set
        var isActive: Boolean = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.APP_EVENT, AnalyticConst.app_close, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEventUtil(EventType.APP_EVENT, AnalyticConst.app_close, "", this)
        isActive = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.APP_EVENT, AnalyticConst.app_open, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEventUtil(EventType.APP_EVENT, AnalyticConst.app_open, "", this)
        isActive = true
    }
}