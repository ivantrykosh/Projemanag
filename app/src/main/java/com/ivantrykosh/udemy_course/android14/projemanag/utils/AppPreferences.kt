package com.ivantrykosh.udemy_course.android14.projemanag.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private const val PREFS_NAME = "projemanag_prefs"

    private lateinit var sharedPreferences: SharedPreferences

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    var fcmTokenUpdated: Boolean?
        get() = Key.FCM_TOKEN_UPDATED.getBoolean()
        set(value) = Key.FCM_TOKEN_UPDATED.setBoolean(value)

    var fcmToken: String?
        get() = Key.FCM_TOKEN.getString()
        set(value) = Key.FCM_TOKEN.setString(value)

    private enum class Key {
        FCM_TOKEN_UPDATED, FCM_TOKEN;

        fun getString(): String? = if (sharedPreferences.contains(name)) sharedPreferences.getString(name, "") else null
        fun setString(value: String?) = value?.let { sharedPreferences.edit { putString(name, value) } } ?: remove()

        fun getBoolean(): Boolean? = if (sharedPreferences.contains(name)) sharedPreferences.getBoolean(name, false) else null
        fun setBoolean(value: Boolean?) = value?.let { sharedPreferences.edit { putBoolean(name, value) } } ?: remove()

        private fun remove() = sharedPreferences.edit { remove(name) }
    }
}