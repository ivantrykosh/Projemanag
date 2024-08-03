package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirestoreCollections
import kotlinx.coroutines.tasks.await

class FirestoreUser {
    private val mFirestore = FirebaseFirestore.getInstance()

    suspend fun createUser(user: User, userId: String) {
        mFirestore.collection(FirestoreCollections.USERS)
            .document(userId)
            .set(user, SetOptions.merge())
            .await()
    }

    suspend fun getUserById(userId: String): User {
        return mFirestore.collection(FirestoreCollections.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)!!
    }

    suspend fun updateUser(userId: String, userData: HashMap<String, Any>) {
        mFirestore.collection(FirestoreCollections.USERS)
            .document(userId)
            .update(userData)
            .await()
    }

    suspend fun getUserByEmail(email: String): User {
        return mFirestore.collection(FirestoreCollections.USERS)
            .whereEqualTo(User.FIELDS.EMAIL, email)
            .get()
            .await()
            .toObjects(User::class.java)[0]
    }

    suspend fun getUsersByIds(usersIds: List<String>): List<User> {
        return mFirestore.collection(FirestoreCollections.USERS)
            .whereIn(User.FIELDS.EMAIL, usersIds)
            .get()
            .await()
            .toObjects(User::class.java)
    }
}