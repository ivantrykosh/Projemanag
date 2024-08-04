package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.tasks.await

class FirebaseInstance {
    private val mFirebaseInstance = FirebaseInstanceId.getInstance()

    suspend fun getToken(): String {
        val instanceId = mFirebaseInstance.instanceId.await()
        val token = instanceId.token
        return token
    }
}