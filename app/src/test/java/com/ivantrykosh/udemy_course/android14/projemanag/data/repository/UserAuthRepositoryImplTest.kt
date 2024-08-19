package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.stubbing.Answer

@RunWith(MockitoJUnitRunner::class)
class UserAuthRepositoryImplTest {

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var userAuthRepository: UserAuthRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseAuth = mock()
        userAuthRepository = UserAuthRepositoryImpl(mockFirebaseAuth)
    }

    @Test
    fun getCurrentUserId_success() = runTest {
        val currentUserId = "testUserId"

        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(currentUserId)
        val retrievedUserId = userAuthRepository.getCurrentUserId()

        assertEquals(currentUserId, retrievedUserId)
    }

    @Test
    fun signOut_success() = runTest {
        whenever(mockFirebaseAuth.signOut()).doAnswer(Answer {  })
        userAuthRepository.signOut()

        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun createUser_success() = runTest {
        val email = "testEmail"
        val password = "testPassword"

        whenever(mockFirebaseAuth.createUser(email, password)).doReturn(Unit)
        userAuthRepository.createUser(email, password)

        verify(mockFirebaseAuth).createUser(email, password)
    }

    @Test
    fun signIn_success() = runTest {
        val email = "testEmail"
        val password = "testPassword"

        whenever(mockFirebaseAuth.signIn(email, password)).doReturn(Unit)
        userAuthRepository.signIn(email, password)

        verify(mockFirebaseAuth).signIn(email, password)
    }
}