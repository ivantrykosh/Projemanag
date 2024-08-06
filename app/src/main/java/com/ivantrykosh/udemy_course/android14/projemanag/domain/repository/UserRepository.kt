package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User

interface UserRepository {

    /**
     * Create new user (after storing credentials in Firebase Auth)
     */
    suspend fun createUser(user: User)

    /**
     * Get current user data
     */
    suspend fun getCurrentUser(): User

    /**
     * Update current user data using Map that contains User field as key and data as value
     */
    suspend fun updateUser(userData: Map<String, Any>)

    /**
     * Get user data by email
     */
    suspend fun getUserByEmail(email: String): User

    /**
     * Get users by their IDs
     */
    suspend fun getUsersByIds(userIds: List<String>): List<User>
}