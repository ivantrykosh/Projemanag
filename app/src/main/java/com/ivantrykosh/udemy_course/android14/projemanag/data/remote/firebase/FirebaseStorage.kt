package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorage(private val mFirebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()) {

    suspend fun uploadImage(newName: String, imageUri: Uri): String {
        val ref = mFirebaseStorage.reference.child(newName)
        val taskSnapshot = ref.putFile(imageUri).await()
        val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl.await()
        return downloadUrl.toString()
    }
}