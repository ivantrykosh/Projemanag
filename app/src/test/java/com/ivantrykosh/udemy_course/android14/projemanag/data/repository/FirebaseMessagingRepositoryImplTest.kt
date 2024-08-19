package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseMessaging
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class FirebaseMessagingRepositoryImplTest {

    private lateinit var mockFirebaseMessaging: FirebaseMessaging
    private lateinit var firebaseMessagingRepository: FirebaseMessagingRepositoryImpl

    @Before
    fun setup() {
        mockFirebaseMessaging = mock()
        firebaseMessagingRepository = FirebaseMessagingRepositoryImpl(mockFirebaseMessaging)
    }

    @Test
    fun getToken_success() = runTest {
        val token = "testToken"

        whenever(mockFirebaseMessaging.getToken()).doReturn(token)
        val retrievedToken = firebaseMessagingRepository.getToken()

        assertEquals(token, retrievedToken)
    }
}