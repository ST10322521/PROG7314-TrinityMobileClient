package Emeris.PROG7314.trinitymobileclient.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

object TeamServerSession {
    private const val TAG = "teamServerAuth"
    private const val PREFS = "teamserver_session"
    private const val KEY_USERNAME = "username"

    private var prefs: SharedPreferences? = null

    var username: String? = null
        private set

    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        username = prefs?.getString(KEY_USERNAME, null)
        Log.d(TAG, "session restored = ${username ?: "none"}")
    }

    fun start(context: Context, name: String) {
        init(context)
        username = name
        prefs?.edit { putString(KEY_USERNAME, name) }
        Log.d(TAG, "session started for $name")
    }

    fun clear() {
        if (username != null) Log.d(TAG, "session cleared for $username")
        username = null
        prefs?.edit { remove(KEY_USERNAME) }
    }

    fun userId(): String? = username?.let { "teamserver:$it" }
}
