package sg.prelens.jinny.features.splashscreen

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.JinnyApplication
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.features.auth.AuthActivity
import sg.prelens.jinny.features.main.MainActivity
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.prelens.jinny.utils.SharePreference
import sg.vinova.trackingtool.model.EventType
import tech.linjiang.pandora.preference.SharedPref

private const val ACTIVITY_AUTH = 1000

private var roomDB: RoomDB? = null

class SplashedActivity : AppCompatActivity() {

    fun trackingEvent() {
        if (!TrackingHelper.hasConnection(this)) {
            saveDB()
        } else {
            TrackingHelper.sendListEvent()?.observe(this, Observer {
                TrackingHelper.deleteDB(roomDB ?: return@Observer)
            })
        }

//        TrackingHelper.sendEvent(EventType.APP_EVENT, AnalyticConst.app_open, "", this)?.observe(this, Observer {
//            if (it?.state == State.FAILED) {
//                saveDB()
//            }
//        })

        if (SharePreference.getInstance().getFirstTimeApp() == null) {
            if (!TrackingHelper.hasConnection(this)) {
                val default = DefaultAnalytics(EventType.APP_EVENT, AnalyticConst.app_open_first_time, description = null)
                TrackingHelper.setDB(default, roomDB ?: return, this)
            }
            TrackingHelper.sendEvent(EventType.APP_EVENT, AnalyticConst.app_open_first_time, "", this)?.observe(this, Observer {
                if (it?.state == State.FAILED) {
                    saveDB()
                }
            })
            SharePreference.getInstance().saveFirstTimeApp()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // setTheme(R.style.AppTheme_Base)
//        FirebaseAnalyticsUtil.putDefaultAnalytics(this, AnalyticConst.track_app_open)

        roomDB = RoomDB.newInstance(this)

        trackingEvent()

        if (!isAuthenticated() && !isLoginAsGuest()) {
            if (intent != null) {
                dumpIntentNotLogin(intent)
            }
            startActivityForResult(Intent(this, AuthActivity::class.java), ACTIVITY_AUTH)
        } else {
            if (intent == null) {
                startActivity(intentFor<MainActivity>()
                        .putExtra("page", 1))
            } else {
                dumpIntent(intent)
            }

        }


        super.onCreate(savedInstanceState)
        finish()
    }

    fun dumpIntent(intent: Intent?) {
        val url = intent?.dataString
        val data = url?.substringAfterLast("/")?.split("&")
        if (!data?.get(0).equals("shareapp")) {
            val screen = data?.get(0)?.substringAfterLast("=")
            val id = data?.get(1)?.substringAfterLast("=")
            startActivity(intentFor<MainActivity>()
                    .putExtra("page", 1)
                    .putExtra("screen", screen)
                    .putExtra("id", id))
        } else {
            startActivity(intentFor<MainActivity>()
                    .putExtra("page", 1))
        }
    }

    fun dumpIntentNotLogin(intent: Intent?) {
        val url = intent?.dataString
        val data = url?.substringAfterLast("/")?.split("&")
        if (!data?.get(0).equals("shareapp")) {
            val screen = data?.get(0)?.substringAfterLast("=")
            val id = data?.get(1)?.substringAfterLast("=")

            SharePreference.getInstance().saveShareDeal(screen, id)
        }
    }

    fun saveDB() {
        val default = DefaultAnalytics(EventType.APP_EVENT, AnalyticConst.app_open, description = null)
        TrackingHelper.setDB(default, roomDB ?: return, this)
    }

    private fun isAuthenticated(): Boolean {
        return (application as JinnyApplication).currentUser != null
    }

    private fun isLoginAsGuest(): Boolean {
        return SharePreference.getInstance().getGuestAccount() != null
    }

    private fun onAuthenticatedCallback(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_CANCELED -> finish()
            Activity.RESULT_OK -> recreate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ACTIVITY_AUTH -> onAuthenticatedCallback(resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}