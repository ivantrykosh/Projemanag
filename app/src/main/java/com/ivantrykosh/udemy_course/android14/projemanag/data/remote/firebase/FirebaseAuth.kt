package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuth {
    private val mFirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String {
        return mFirebaseAuth.currentUser?.uid ?: ""
    }

    fun signOut() {
        mFirebaseAuth.signOut()
    }

    suspend fun createUser(email: String, password: String) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signIn(email: String, password: String) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password).await()
    }
}