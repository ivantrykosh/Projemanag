package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage

import android.net.Uri
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class UploadImageUseCaseTest {

    private lateinit var uploadImageUseCase: UploadImageUseCase

    @Before
    fun setup() {
        uploadImageUseCase = UploadImageUseCase(FirebaseStorageRepositoryImpl)
    }

    @Test
    fun uploadImage_success() = runBlocking {
        val newName = "name"
        val uri = mock<Uri>()
        var downloadUrl = ""

        uploadImageUseCase(newName, uri).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { downloadUrl = result.data!! }
            }
        }

        assertEquals(FirebaseStorageRepositoryImpl.testDownloadUrl, downloadUrl)
    }

    @Test
    fun uploadImage_wrongNewName() = runBlocking {
        val newName = ""
        val uri = mock<Uri>()

        uploadImageUseCase(newName, uri).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test
    fun uploadImage_wrongUri() = runBlocking {
        val newName = "name"
        val uri = mock<Uri> {
            on { it.toString() } doReturn ""
        }

        uploadImageUseCase(newName, uri).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun uploadImage_checkFirstEmit() = runBlocking {
        val newName = "name"
        val uri = mock<Uri>()

        uploadImageUseCase(newName, uri).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}