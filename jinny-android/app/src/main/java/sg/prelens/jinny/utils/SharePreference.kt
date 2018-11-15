package sg.prelens.jinny.utils

import android.content.Context
import sg.prelens.jinny.models.ProfileUser
import android.content.SharedPreferences
import android.support.annotation.Nullable
import com.google.gson.Gson

/**
 * Author      : BIMBIM<br>.
 * Create Date : 4/26/18<br>.
 */
class SharePreference private constructor() {
    private var mSharedPreferences: SharedPreferences? = null
    private var mContext: Context? = null

    companion object {
        var sInstance: SharePreference? = null
        @JvmStatic
        fun getInstance(): SharePreference {
            if (sInstance == null) {
                sInstance = SharePreference()
            }
            return sInstance as SharePreference
        }
    }

    fun getContext(context: Context): SharePreference {
        mContext = context.applicationContext
        return this
    }

    fun getSharedPreferences(): SharedPreferences? {
        mSharedPreferences = mContext!!.getSharedPreferences("jinny", Context.MODE_PRIVATE)
        return mSharedPreferences
    }

    fun saveSharedPreferences(): SharedPreferences.Editor {
        return mSharedPreferences!!.edit()
    }

    fun getString(key: String, value: String): String? {
        return mSharedPreferences!!.getString(key, value)
    }

    fun saveGuestAccount(guest: ProfileUser) {
        saveSharedPreferences().putString("id", guest.id).commit()
        saveSharedPreferences().putString("email", guest.email).commit()
        saveSharedPreferences().putString("dob", guest.dob).commit()
        saveSharedPreferences().putString("gender", guest.gender).commit()
        saveSharedPreferences().putString("token", guest.token).commit()
        saveSharedPreferences().putBoolean("isFirstTimeCashBack", guest.isFirstTime_CashBack).commit()
        saveSharedPreferences().putBoolean("isFirstTimeMembership", guest.isFirstTime_Membership).commit()
        saveSharedPreferences().putString("residential_region", guest.residential_region?.name).commit()
        saveSharedPreferences().putBoolean("guest", true).commit()
    }

    fun deleteGuestAccount() {
        val pref = mContext!!.getSharedPreferences("jinny", Context.MODE_PRIVATE)
        pref?.edit()?.clear()?.apply()
    }

    fun turnGuestToFull(email: String) {
        saveSharedPreferences().putString("email", email).commit()
        saveSharedPreferences().putBoolean("guest", false).commit()
    }

    fun saveFirstTimeCashBack(isFirstTime: Boolean) {
        saveSharedPreferences().putBoolean("isFirstTimeCashBack", isFirstTime).commit()
    }

    fun saveFirstTimeMembership(isFirstTime: Boolean) {
        saveSharedPreferences().putBoolean("isFirstTimeMembership", isFirstTime).commit()
    }

    fun saveShareDeal(screen: String?, id: String?) {
        saveSharedPreferences().putString("screen", screen ?: "").commit()
        saveSharedPreferences().putString("id", id ?: "").commit()
    }

    fun getScreenShareDeal(): String? {
        return mSharedPreferences?.getString("screen", null)
    }

    fun getIdShareDeal(): String? {
        return mSharedPreferences?.getString("id", null)
    }

    fun getGuestAccount(): ProfileUser? {
        val id = mSharedPreferences?.getString("id", null)
        val email = mSharedPreferences?.getString("email", null)
        val dob = mSharedPreferences?.getString("dob", null)
        val gender = mSharedPreferences?.getString("gender", null)
        val token = mSharedPreferences?.getString("token", null)
        val isFirstTime_CashBack = mSharedPreferences?.getBoolean("isFirstTimeCashBack", false)!!
        val isFirstTime_Membership = mSharedPreferences?.getBoolean("isFirstTimeMembership", false)!!
        val residential_region = mSharedPreferences?.getString("residential_region", null)
        val guest = mSharedPreferences?.getBoolean("guest", false)
        if (email == null || token == null) return null
        return ProfileUser(id, email, dob, gender, token, isFirstTime_CashBack, isFirstTime_Membership, guest)
    }

    fun saveFirstTimeApp() {
        saveSharedPreferences().putString("firstTimeApp", "false").commit()
    }

    fun getFirstTimeApp(): String? {
        return mSharedPreferences?.getString("firstTimeApp", null)
    }
}
