package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignInUseCaseTest {

    private lateinit var signIdUseCase: SignInUseCase

    @Before
    fun setup() {
        signIdUseCase = SignInUseCase(UserAuthRepositoryImpl)
    }

    @Test
    fun signIn_success() = runBlocking {
        val email = UserAuthRepositoryImpl.testEmail
        val password = UserAuthRepositoryImpl.testPassword

        signIdUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun signIn_wrongEmail() = runBlocking {
        val email = "wrong@email.com"
        val password = UserAuthRepositoryImpl.testPassword

        signIdUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test
    fun signIn_wrongPassword() = runBlocking {
        val email = UserAuthRepositoryImpl.testEmail
        val password = "wrongPassword"

        signIdUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun signIn_checkFirstEmit() = runBlocking {
        val email = UserAuthRepositoryImpl.testEmail
        val password = UserAuthRepositoryImpl.testPassword

        signIdUseCase(email, password).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}