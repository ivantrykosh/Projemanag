package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignOutUseCaseTest {

    private lateinit var signOutUseCase: SignOutUseCase

    @Before
    fun setup() {
        signOutUseCase = SignOutUseCase(UserAuthRepositoryImpl)
    }

    @After
    fun setCurrentUserId() {
        UserAuthRepositoryImpl.currentUserId = "testUserId"
    }

    @Test
    fun signOut_success() = runBlocking {
        signOutUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }

        assertTrue(UserAuthRepositoryImpl.currentUserId.isEmpty())
    }

    @Test(expected = CancellationException::class)
    fun signOut_checkFirstEmit() = runBlocking {
        signOutUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}