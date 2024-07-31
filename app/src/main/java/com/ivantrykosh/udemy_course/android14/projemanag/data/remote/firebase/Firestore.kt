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

    suspend fun getUserData(userId: String): User {
        return mFirestore.collection(FirebaseCollections.USERS)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)!!
    }

    suspend fun updateUserData(userId: String, userData: HashMap<String, Any>) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(userId)
            .update(userData)
            .await()
    }
}