package sg.prelens.jinny.utils

import android.annotation.SuppressLint
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.exts.getCurrentUser

class FirebaseAnalyticsUtil {

    companion object {
        private var firebaseAnalytics: FirebaseAnalytics? = null

        private fun newInstance(context: Context) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        }

        fun putDefaultAnalytics(context: Context, event: String?) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            val bundle = Bundle()
            bundle.putString(AnalyticConst.user_email, context.getCurrentUser()?.email)
            bundle.putString(AnalyticConst.timestamp, getCurrentTimestamp()?.toString())
            firebaseAnalytics?.logEvent(event ?: return, bundle)
        }

        fun putActionParameterAnalytics(context: Context, event: String?, parameter: String?, value: String?) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            val bundle = Bundle()
            bundle.putString(parameter, value)
            firebaseAnalytics?.logEvent(event ?: return, bundle)
        }

        fun putActionParameterDoubleAnalytics(context: Context, event: String?, first: String?, valueFirst: String?, second: String?, valueSecond: String?) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            val bundle = Bundle()
            bundle.putString(first, valueFirst)
            bundle.putString(second, valueSecond)
            firebaseAnalytics?.logEvent(event ?: return, bundle)
        }

        fun putActionParameterTripleAnalytics(context: Context, event: String?, first: String?, valueFirst: String?, second: String?, valueSecond: String?,
                                              third: String?, valueThird: String?) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            val bundle = Bundle()
            bundle.putString(first, valueFirst)
            bundle.putString(second, valueSecond)
            bundle.putString(third, valueThird)
            firebaseAnalytics?.logEvent(event ?: return, bundle)
        }

        fun putLoginPropertyAnalytics(context: Context) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_email, context.getCurrentUser()?.email)
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_id, context.getCurrentUser()?.id)
        }

        fun putLoginPropertyGuestAnalytics(context: Context) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            val profile = SharePreference.getInstance().getGuestAccount()
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_email, profile?.email)
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_id, profile?.id)
        }

        fun putLogOutPropertyAnalytics(context: Context) {
            if (firebaseAnalytics == null) {
                newInstance(context)
            }
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_email, "")
            firebaseAnalytics?.setUserProperty(AnalyticConst.jinny_user_id, "")
        }

        @SuppressLint("NewApi")
        fun getCurrentTimestamp(): Long? {
            return Calendar.getInstance().timeInMillis
        }
    }
}