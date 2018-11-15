package sg.prelens.jinny.features.cashbackinfo

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.layout_toolbar_detail.*
import kotlinx.android.synthetic.main.activity_terms.*
import sg.prelens.jinny.R
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType

class TermsActivity : AppCompatActivity() {

    private var roomDB: RoomDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        roomDB = RoomDB.newInstance(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_terms_and_conditions, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.app_terms_and_conditions, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.app_terms_and_conditions, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })

        tvTitle.text = getString(R.string.terms)
        ivMid.setOnClickListener {
            onBackPressed()
        }
        ivBookmark?.visibility = View.GONE
        wvTerms.loadUrl("file:///android_asset/htmltermsandpolicy/terms.html")
    }
}
