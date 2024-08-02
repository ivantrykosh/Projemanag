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

    /**
     * Create user with email and password is Firebase Auth
     */
    suspend fun createUser(email: String, password: String)

    /**
     * Sign in user with email and password
     */
    suspend fun signIn(email: String, password: String)
}