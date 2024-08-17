package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class FirebaseMessagingTest {

    private lateinit var mockFirebaseMessaging: FirebaseMessaging
    private lateinit var messaging: com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseMessaging

    @Before
    fun setup() {
        mockFirebaseMessaging = mock()
        messaging = FirebaseMessaging(mockFirebaseMessaging)
    }

    @Test
    fun getToken_success() = runTest {
        val token = "myFCMToken"
        val completedTask = Tasks.forResult(token)

        mock<Task<String>> {
            on { mockFirebaseMessaging.token } doReturn completedTask
        }

        assertEquals(token, messaging.getToken())
    }
}