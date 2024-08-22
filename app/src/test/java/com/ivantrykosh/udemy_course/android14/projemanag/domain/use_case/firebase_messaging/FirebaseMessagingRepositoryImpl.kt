package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_messaging

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseMessagingRepository

object FirebaseMessagingRepositoryImpl : FirebaseMessagingRepository {
    val testToken = "testToken"

    override suspend fun getToken(): String {
        return testToken
    }
}