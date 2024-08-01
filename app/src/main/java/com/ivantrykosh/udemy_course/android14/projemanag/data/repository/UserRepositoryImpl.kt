package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.Firestore
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: Firestore,
    private val firebaseAuth: FirebaseAuth
): UserRepository {

    override suspend fun createUser(user: User) {
        val userId = firebaseAuth.getCurrentUserId()
        firestore.createUser(user, userId)
    }

    override suspend fun getCurrentUserData(): User {
        val userId = firebaseAuth.getCurrentUserId()
        val user = firestore.getUserData(userId)
        return user
    }

    override suspend fun updateUserData(userData: HashMap<String, Any>) {
        val userId = firebaseAuth.getCurrentUserId()
        firestore.updateUserData(userId, userData)
    }
}