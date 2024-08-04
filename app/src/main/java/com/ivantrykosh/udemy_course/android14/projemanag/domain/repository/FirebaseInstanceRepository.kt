package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

interface FirebaseInstanceRepository {

    /**
     * Get token for FCM
     */
    suspend fun getToken(): String
}