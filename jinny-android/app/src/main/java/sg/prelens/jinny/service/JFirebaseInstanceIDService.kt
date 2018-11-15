package sg.prelens.jinny.service

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.features.settings.SettingPrefs


class JFirebaseInstanceIDService : FirebaseInstanceIdService(), LifecycleOwner {
    lateinit var lifecycleRegistry : LifecycleRegistry
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        lifecycleRegistry.markState(Lifecycle.State.STARTED)

    }
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        // This call will in background thread
        // so doesn't need to use viewmodel
//        ServiceLocator.instance(this)
//                .getAuthRepository().changeFirebaseToken(SettingPrefs(this).oldFCMToken,
//                FirebaseInstanceId.getInstance().token ?: "",errorLiveData)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}
