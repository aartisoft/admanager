package com.admanager.sample

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val PREFS_FILENAME = "com.admanager.sample.prefs"
    private const val SPLASH_TUTOR_SHOWED = "SPLASH_TUTOR_SHOWED"
    private const val SOMEINTEGER = "SOMEINTEGER"

    private lateinit var prefs: SharedPreferences;

    @JvmStatic
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    }

    var someInteger: Int
        get() = prefs.getInt(SOMEINTEGER, 0)
        set(value) {
            prefs.edit().putInt(SOMEINTEGER, value).apply()
        }

    var splash_tutor_showed: Boolean
        get() = prefs.getBoolean(SPLASH_TUTOR_SHOWED, false)
        set(value) {
            prefs.edit().putBoolean(SPLASH_TUTOR_SHOWED, value).apply()
        }

}
