package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import android.net.Uri
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseStorage
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseStorageRepository
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): FirebaseStorageRepository {

    override suspend fun uploadImage(newName: String, imageUri: Uri): String {
        return firebaseStorage.uploadImage(newName, imageUri)
    }
}