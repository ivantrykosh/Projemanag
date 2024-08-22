package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage

import android.net.Uri
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseStorageRepository

object FirebaseStorageRepositoryImpl : FirebaseStorageRepository {
    val testDownloadUrl = "testDownloadUrl"

    override suspend fun uploadImage(newName: String, imageUri: Uri): String {
        if (newName.isEmpty()) {
            throw Exception("New name of image is incorrect")
        } else if (imageUri.toString().isEmpty()) {
            throw Exception("Uri of image is incorrect")
        }

        return testDownloadUrl
    }
}