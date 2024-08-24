package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
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
class GetUserByEmailUseCaseTest {

    private lateinit var getUserByEmailUseCase: GetUserByEmailUseCase

    @Before
    fun setup() {
        getUserByEmailUseCase = GetUserByEmailUseCase(UserRepositoryImpl)
    }

    @Test
    fun getUserByEmail_success() = runBlocking {
        val email = UserRepositoryImpl.testEmail
        var retrievedUser = User()

        getUserByEmailUseCase(email).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { retrievedUser = result.data!! }
            }
        }

        assertEquals(email, retrievedUser.email)
    }

    @Test
    fun getUserByEmail_wrongEmail() = runBlocking {
        val email = "incorrect@email.com"

        getUserByEmailUseCase(email).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun getUserByEmail_checkFirstEmit() = runBlocking {
        val email = UserRepositoryImpl.testEmail

        getUserByEmailUseCase(email).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}