package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

interface UserAuthRepository {

    /**
     * Get current user ID from Firestore
     */
    suspend fun getCurrentUserId(): String

    /**
     * Sign out user using Firebase
     */
    suspend fun signOut()
}