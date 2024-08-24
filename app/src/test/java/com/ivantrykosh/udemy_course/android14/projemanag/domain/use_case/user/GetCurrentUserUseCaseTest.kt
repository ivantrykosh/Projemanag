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
class GetCurrentUserUseCaseTest {

    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Before
    fun setup() {
        getCurrentUserUseCase = GetCurrentUserUseCase(UserRepositoryImpl)
    }

    @Test
    fun getCurrentUser_success() = runBlocking {
        var retrievedUser = User()

        getCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { retrievedUser = result.data!! }
            }
        }

        assertEquals(UserRepositoryImpl.testUserId, retrievedUser.id)
    }

    @Test(expected = CancellationException::class)
    fun getCurrentUser_checkFirstEmit() = runBlocking {
        getCurrentUserUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}