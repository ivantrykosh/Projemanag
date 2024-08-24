package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.UserAuthRepositoryImpl
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
class CreateUserUseCaseTest {

    private lateinit var createUserUseCase: CreateUserUseCase

    @Before
    fun setup() {
        createUserUseCase = CreateUserUseCase(UserRepositoryImpl, UserAuthRepositoryImpl)
    }

    @Test
    fun createUser_success() = runBlocking {
        val email = "test@email.com"
        val password = "testPassword"
        val name = "Test Name"

        createUserUseCase(email, password, name).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun createUser_wrongEmail() = runBlocking {
        val email = ""
        val password = "testPassword"
        val name = "Test Name"

        createUserUseCase(email, password, name).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test
    fun createUser_wrongPassword() = runBlocking {
        val email = "test@email.com"
        val password = ""
        val name = "Test Name"

        createUserUseCase(email, password, name).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test
    fun createUser_wrongName() = runBlocking {
        val email = "test@email.com"
        val password = "testPassword"
        val name = ""

        createUserUseCase(email, password, name).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun createUser_checkFirstEmit() = runBlocking {
        val email = "test@email.com"
        val password = "testPassword"
        val name = "Test Name"

        createUserUseCase(email, password, name).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}