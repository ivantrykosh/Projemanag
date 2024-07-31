package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth

class FirebaseAuth {
    private val mFirebaseAuth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String {
        return mFirebaseAuth.currentUser?.uid ?: ""
    }

    fun signOut() {
        mFirebaseAuth.signOut()
    }
}