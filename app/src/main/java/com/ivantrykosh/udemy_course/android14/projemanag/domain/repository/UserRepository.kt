package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User

interface UserRepository {

    /**
     * Create new user (after credentials stored in Firebase Auth)
     */
    suspend fun createUser(user: User)

    /**
     * Get current user data
     */
    suspend fun getCurrentUserData(): User

    /**
     * Update current user data using HashMap that contains User field as key and data as value
     */
    suspend fun updateUserData(userData: HashMap<String, Any>)
}