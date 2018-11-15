package sg.prelens.jinny.exts

import android.content.Context
import android.content.Context.MODE_PRIVATE
import sg.prelens.jinny.models.ProfileUser

/**
 * Created by tommy on 3/14/18.
 */
fun Context.saveUserPref(user: ProfileUser) {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    val editor = pref.edit()
    editor.putString("id", user.id)
    editor.putString("email", user.email)
    editor.putString("dob", user.dob)
    editor.putString("gender", user.gender)
    editor.putString("token", user.token)
    editor.putBoolean("isFirstTimeCashBack", user.isFirstTime_CashBack)
    editor.putBoolean("isFirstTimeMembership", user.isFirstTime_Membership)
    editor.putString("residential_region", user.residential_region?.name)
    editor.apply()
}

fun Context.getCurrentUser(): ProfileUser? {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    val id = pref.getString("id", null)
    val email = pref.getString("email", null)
    val dob = pref.getString("dob", null)
    val gender = pref.getString("gender", null)
    val token = pref.getString("token", null)
    val isFirstTime_CashBack = pref.getBoolean("isFirstTimeCashBack", false)
    val isFirstTime_Membership = pref.getBoolean("isFirstTimeMembership", false)
    val residential_region = pref.getString("residential_region", null)
    if (email == null || token == null) return null
    return ProfileUser(id, email, dob, gender, token, isFirstTime_CashBack, isFirstTime_Membership)
}

fun Context.getUserToken(): String? {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    return pref.getString("token", null)
}

fun Context.saveFirstTimeCashBack(isFirstTime : Boolean) {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    val editor = pref.edit()
    editor.putBoolean("isFirstTimeCashBack", isFirstTime)
    editor.apply()
}

fun Context.saveFirstTimeMembership(isFirstTime : Boolean) {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    val editor = pref.edit()
    editor.putBoolean("isFirstTimeMembership", isFirstTime)
    editor.apply()
}

fun Context.deleteUserToken() {
    val pref = getSharedPreferences("jinny", MODE_PRIVATE)
    pref.edit().clear().apply()
}
