package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreUser
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestoreUser: FirestoreUser,
    private val firebaseAuth: FirebaseAuth
): UserRepository {

    override suspend fun createUser(user: User) {
        val userId = firebaseAuth.getCurrentUserId()
        firestoreUser.createUser(user, userId)
    }

    override suspend fun getCurrentUser(): User {
        val userId = firebaseAuth.getCurrentUserId()
        val user = firestoreUser.getUserById(userId)
        return user
    }

    override suspend fun updateUser(userData: Map<String, Any>) {
        val userId = firebaseAuth.getCurrentUserId()
        firestoreUser.updateUser(userId, userData)
    }

    override suspend fun getUserByEmail(email: String): User {
        return firestoreUser.getUserByEmail(email)
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        return firestoreUser.getUsersByIds(userIds)
    }
}