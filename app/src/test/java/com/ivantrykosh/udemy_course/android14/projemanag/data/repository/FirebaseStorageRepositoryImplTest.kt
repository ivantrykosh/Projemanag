package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import android.net.Uri
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class FirebaseStorageRepositoryImplTest {

    private lateinit var mockFirebaseStorage: FirebaseStorage
    private lateinit var firebaseStorageRepository: FirebaseStorageRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseStorage = mock()
        firebaseStorageRepository = FirebaseStorageRepositoryImpl(mockFirebaseStorage)
    }

    @Test
    fun uploadImage_success() = runTest {
        val newName = "testNewName"
        val imageUri = mock<Uri>()
        val downloadUrl = "testUrl"

        whenever(mockFirebaseStorage.uploadImage(newName, imageUri)).doReturn(downloadUrl)
        val retrievedUrl = firebaseStorageRepository.uploadImage(newName, imageUri)

        assertEquals(downloadUrl, retrievedUrl)
    }
}