package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseMessaging {
    private val mFirebaseMessaging = FirebaseMessaging.getInstance()

    suspend fun getToken(): String {
        return mFirebaseMessaging.token.await()
    }
}