package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseMessaging
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseMessagingRepository
import javax.inject.Inject

class FirebaseMessagingRepositoryImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
): FirebaseMessagingRepository {

    override suspend fun getToken(): String {
        return firebaseMessaging.getToken()
    }
}