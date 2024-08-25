package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

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
class GetCurrentUserIdUseCaseTest {

    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Before
    fun setup() {
        getCurrentUserIdUseCase = GetCurrentUserIdUseCase(UserAuthRepositoryImpl)
    }

    @Test
    fun getCurrentUserId_success() = runBlocking {
        var testUserId = ""

        getCurrentUserIdUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { testUserId = result.data!! }
            }
        }

        assertEquals(UserAuthRepositoryImpl.currentUserId, testUserId)
    }

    @Test(expected = CancellationException::class)
    fun getCurrentUserId_checkFirstEmit() = runBlocking {
        getCurrentUserIdUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}