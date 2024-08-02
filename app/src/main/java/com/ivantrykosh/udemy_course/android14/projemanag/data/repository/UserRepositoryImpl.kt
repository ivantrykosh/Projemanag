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

    override suspend fun getCurrentUser(): User {
        val userId = firebaseAuth.getCurrentUserId()
        val user = firestore.getUserById(userId)
        return user
    }

    override suspend fun updateUser(userData: HashMap<String, Any>) {
        val userId = firebaseAuth.getCurrentUserId()
        firestore.updateUser(userId, userData)
    }

    override suspend fun getUserByEmail(email: String): User {
        return firestore.getUserByEmail(email)
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        return firestore.getUsersByIds(userIds)
    }
}