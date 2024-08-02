package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirebaseCollections
import kotlinx.coroutines.tasks.await

class Firestore {
    private val mFirestore = FirebaseFirestore.getInstance()

    suspend fun createUser(user: User, userId: String) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(userId)
            .set(user, SetOptions.merge())
            .await()
    }

    suspend fun getUserById(userId: String): User {
        return mFirestore.collection(FirebaseCollections.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)!!
    }

    suspend fun updateUser(userId: String, userData: HashMap<String, Any>) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(userId)
            .update(userData)
            .await()
    }

    suspend fun getUserByEmail(email: String): User {
        return mFirestore.collection(FirebaseCollections.USERS)
            .whereEqualTo(User.FIELDS.EMAIL, email)
            .get()
            .await()
            .toObjects(User::class.java)[0]
    }

    suspend fun getUsersByIds(usersIds: List<String>): List<User> {
        return mFirestore.collection(FirebaseCollections.USERS)
            .whereIn(User.FIELDS.EMAIL, usersIds)
            .get()
            .await()
            .toObjects(User::class.java)
    }
}