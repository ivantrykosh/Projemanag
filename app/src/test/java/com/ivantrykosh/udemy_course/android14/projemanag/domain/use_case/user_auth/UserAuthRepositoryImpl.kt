package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository

object UserAuthRepositoryImpl : UserAuthRepository {
    var currentUserId = "testUserId"
    val testEmail = "test@email.com"
    val testPassword = "test@Passw0rd"

    override suspend fun getCurrentUserId(): String {
        return currentUserId
    }

    override suspend fun signOut() {
        currentUserId = ""
    }

    override suspend fun createUser(email: String, password: String) {
        if (email.isEmpty()) {
            throw Exception("Email of user is incorrect")
        } else if (password.length < 6) {
            throw Exception("Password is not strong enough")
        }
    }

    override suspend fun signIn(email: String, password: String) {
        if (email != testEmail) {
            throw Exception("Email of user is incorrect")
        } else if (password != testPassword) {
            throw Exception("Password is incorrect")
        }
    }
}