package br.com.minhaentrada.victor.challenge.data

import android.content.Context
import android.content.SharedPreferences
import br.com.minhaentrada.victor.challenge.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.USER_SESSION_PREFERENCES_FILE, Context.MODE_PRIVATE)

    fun saveSession(isLoggedIn: Boolean, userId: Long) {
        val editor = prefs.edit()
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, isLoggedIn)
        editor.putLong(Constants.KEY_LOGGED_IN_USER_ID, userId)
        editor.apply()
    }

    fun getLoggedInUserId(): Long {
        return prefs.getLong(Constants.KEY_LOGGED_IN_USER_ID, -1L)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}