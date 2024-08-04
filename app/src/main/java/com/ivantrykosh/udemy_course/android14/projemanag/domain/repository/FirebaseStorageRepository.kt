package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

import android.net.Uri

interface FirebaseStorageRepository {

    /**
     * Upload local saved image to Firebase Storage with new name and return download link
     */
    suspend fun uploadImage(newName: String, imageUri: Uri): String
}