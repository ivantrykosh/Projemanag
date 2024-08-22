package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_messaging

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

@RunWith(MockitoJUnitRunner::class)
class GetTokenUseCaseTest {

    private lateinit var getTokenUseCase: GetTokenUseCase

    @Before
    fun setup() {
        getTokenUseCase = GetTokenUseCase(FirebaseMessagingRepositoryImpl)
    }

    @Test
    fun getToken_success() = runBlocking {
        var token = ""

        getTokenUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { token = result.data!! }
            }
        }

        assertEquals(FirebaseMessagingRepositoryImpl.testToken, token)
    }

    @Test(expected = CancellationException::class)
    fun getToken_checkFirstEmit() = runBlocking {
        getTokenUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}