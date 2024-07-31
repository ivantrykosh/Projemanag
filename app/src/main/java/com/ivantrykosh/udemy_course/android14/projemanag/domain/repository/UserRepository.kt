package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User

interface UserRepository {

    /**
     * Create new user
     */
    suspend fun createUser(user: User)

    /**
     * Get user by user ID
     */
    suspend fun getUserData(userId: String): User

    /**
     * Update user data using HashMap that contains User field as key and data as value
     */
    suspend fun updateUserData(userId: String, userData: HashMap<String, Any>)
}