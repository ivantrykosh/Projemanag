package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class FirebaseStorageTest {

    private lateinit var mockFirebaseStorage: FirebaseStorage
    private lateinit var storage: com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseStorage

    @Before
    fun setup() {
        mockFirebaseStorage = mock()
        storage = FirebaseStorage(mockFirebaseStorage)
    }

    @Test
    fun uploadImage_success() = runTest {
        val newName = "testImage.jpg"
        val imageUri = mock<Uri>()
        val downloadUrl = "https://firebase.google.com/$newName"

        val ref = mock<StorageReference> {
            on { mockFirebaseStorage.reference } doReturn mock()
            on { mockFirebaseStorage.reference.child(newName) } doReturn it
        }
        val uploadTask = mock<UploadTask> {
            on { ref.putFile(imageUri) } doReturn it
            on { it.isComplete } doReturn true
            on { it.exception } doReturn null
        }
        val taskSnapshot = mock<UploadTask.TaskSnapshot> {
            on { uploadTask.result } doReturn it
        }
        mock<Uri> {
            onGeneric { taskSnapshot.metadata!! } doReturn mock()
            onGeneric { taskSnapshot.metadata!!.reference!! } doReturn mock()
            on { taskSnapshot.metadata!!.reference!!.downloadUrl } doReturn Tasks.forResult(it)
            on { it.toString() } doReturn downloadUrl
        }

        assertEquals(downloadUrl, storage.uploadImage(newName, imageUri))
    }
}