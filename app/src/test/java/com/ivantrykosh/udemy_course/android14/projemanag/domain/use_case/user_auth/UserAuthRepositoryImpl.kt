package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository

object UserAuthRepositoryImpl : UserAuthRepository {
    override suspend fun getCurrentUserId(): String {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(email: String, password: String) {
        if (email.isEmpty()) {
            throw Exception("Email of user is incorrect")
        } else if (password.length < 6) {
            throw Exception("Password is not strong enough")
        }
    }

    override suspend fun signIn(email: String, password: String) {
        TODO("Not yet implemented")
    }
}