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
class GetUsersByIdsUseCaseTest {

    private lateinit var getUsersByIdsUseCase: GetUsersByIdsUseCase

    @Before
    fun setup() {
        getUsersByIdsUseCase = GetUsersByIdsUseCase(UserRepositoryImpl)
    }

    @Test
    fun getUsersByIds_success() = runBlocking {
        val testUserIds = UserRepositoryImpl.testListOfUsersIds
        var retrievedUsers: List<User> = emptyList()

        getUsersByIdsUseCase(testUserIds).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { retrievedUsers = result.data!! }
            }
        }

        assertEquals(UserRepositoryImpl.testListOfUsers.size, retrievedUsers.size)
        assertEquals(UserRepositoryImpl.testListOfUsers[0].id, retrievedUsers[0].id)
    }

    @Test
    fun getUsersByIds_wrongUserId() = runBlocking {
        val testUserIds = UserRepositoryImpl.testListOfUsersIds.toMutableList()
        testUserIds.add("incorrectUserId")
        var retrievedUsers: List<User> = emptyList()

        getUsersByIdsUseCase(testUserIds).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { retrievedUsers = result.data!! }
            }
        }

        assertEquals(UserRepositoryImpl.testListOfUsers.size, retrievedUsers.size)
        assertEquals(UserRepositoryImpl.testListOfUsers[0].id, retrievedUsers[0].id)
    }

    @Test
    fun getUsersByIds_emptyListOfUsersIds() = runBlocking {
        val testUserIds: List<String> = emptyList()
        var retrievedUsers: List<User>? = null

        getUsersByIdsUseCase(testUserIds).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { retrievedUsers = result.data!! }
            }
        }

        assertEquals(0, retrievedUsers!!.size)
    }

    @Test(expected = CancellationException::class)
    fun getUsersByIds_checkFirstEmit() = runBlocking {
        val testUserIds = UserRepositoryImpl.testListOfUsersIds

        getUsersByIdsUseCase(testUserIds).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}