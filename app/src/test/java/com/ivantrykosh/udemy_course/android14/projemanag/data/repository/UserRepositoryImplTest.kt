package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreUser
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class UserRepositoryImplTest {

    private lateinit var mockFirebaseUser: FirestoreUser
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseUser = mock()
        mockFirebaseAuth = mock()
        userRepository = UserRepositoryImpl(mockFirebaseUser, mockFirebaseAuth)
    }

    @Test
    fun createUser_success() = runTest {
        val user = User()
        val userId = "testUserId"

        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(userId)
        whenever(mockFirebaseUser.createUser(user, userId)).doReturn(Unit)
        userRepository.createUser(user)

        verify(mockFirebaseUser).createUser(user, userId)
    }

    @Test
    fun getCurrentUser_success() = runTest {
        val userId = "testUserId"
        val user = User(id = userId)

        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(userId)
        whenever(mockFirebaseUser.getUserById(userId)).doReturn(user)
        val retrievedUser = userRepository.getCurrentUser()

        assertEquals(userId, retrievedUser.id)
    }

    @Test
    fun updateUser_success() = runTest {
        val userId = "testUserId"
        val userData = mapOf(User.FIELDS.NAME to "testName")

        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(userId)
        whenever(mockFirebaseUser.updateUser(userId, userData)).doReturn(Unit)
        userRepository.updateUser(userData)

        verify(mockFirebaseUser).updateUser(userId, userData)
    }

    @Test
    fun getUserByEmail_success() = runTest {
        val email = "test@email.com"
        val user = User(email = email)

        whenever(mockFirebaseUser.getUserByEmail(email)).doReturn(user)
        val retrievedUser = userRepository.getUserByEmail(email)

        assertEquals(email, retrievedUser.email)
    }

    @Test
    fun getUsersByIds_success() = runTest {
        val usersIds = listOf("testUserId1", "testUserId2")
        val users = listOf(User(id = usersIds[0]), User(id = usersIds[1]))

        whenever(mockFirebaseUser.getUsersByIds(usersIds)).doReturn(users)
        val retrievedUsers = userRepository.getUsersByIds(usersIds)

        assertEquals(users.size, retrievedUsers.size)
        assertEquals(users[0].id, retrievedUsers[0].id)
    }
}