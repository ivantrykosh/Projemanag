package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository
import javax.inject.Inject

class UserAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : UserAuthRepository {

    override suspend fun getCurrentUserId(): String {
        return firebaseAuth.getCurrentUserId()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun createUser(email: String, password: String) {
        firebaseAuth.createUser(email, password)
    }

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signIn(email, password)
    }
}