package com.ivantrykosh.udemy_course.android14.projemanag

import android.app.Application
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import dagger.hilt.android.HiltAndroidApp

@Suppress("SpellCheckingInspection")
@HiltAndroidApp
class ProjemanagApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.setup(this)
    }
}