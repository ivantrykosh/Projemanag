package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
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
class UpdateUserUseCaseTest {

    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Before
    fun setup() {
        updateUserUseCase = UpdateUserUseCase(UserRepositoryImpl)
    }

    @Test
    fun updateUser_success() = runBlocking {
        val userData = mapOf(User.FIELDS.NAME to "New Test Name", User.FIELDS.MOBILE to 332211)

        updateUserUseCase(userData).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun updateUser_wrongData() = runBlocking {
        val userData = mapOf(User.FIELDS.ID to "newTestUserId", User.FIELDS.MOBILE to 332211)

        updateUserUseCase(userData).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun updateUser_checkFirstEmit() = runBlocking {
        val userData = mapOf(User.FIELDS.NAME to "New Test Name", User.FIELDS.MOBILE to 332211)

        updateUserUseCase(userData).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}