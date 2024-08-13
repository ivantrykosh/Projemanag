package com.ivantrykosh.udemy_course.android14.projemanag.presenter.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import dagger.hilt.android.scopes.ServiceScoped

@ServiceScoped
class FirebaseNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }
        remoteMessage.notification?.let {
            Log.d("FCM", "Message notification body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendRegistration(token)
    }

    private fun sendRegistration(token: String?) {
        AppPreferences.setup(this)
        AppPreferences.fcmToken = token
    }
}