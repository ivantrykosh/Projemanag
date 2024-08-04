package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage

import android.net.Uri
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseStorageRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val firebaseStorageRepository: FirebaseStorageRepository
) {
    operator fun invoke(newName: String, imageUri: Uri) = flow {
        try {
            emit(Resource.Loading())
            val downloadUrl = firebaseStorageRepository.uploadImage(newName, imageUri)
            emit(Resource.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}