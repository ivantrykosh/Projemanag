package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.junit.Assert.*
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirebaseAuthTest {

    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var auth: com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth

    @Before
    fun setup() {
        mockFirebaseAuth = mock()
        auth = FirebaseAuth(mockFirebaseAuth)
    }

    @Test
    fun getCurrentUserId_userLoggedIn_success() {
        val userId = "userid123"

        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn it
            on { it.uid } doReturn userId
        }

        assertEquals(userId, auth.getCurrentUserId())
    }

    @Test
    fun getCurrentUserId_userNotLoggedIn_success() {
        mock<FirebaseUser> {
            on { mockFirebaseAuth.currentUser } doReturn null
        }

        assertEquals("", auth.getCurrentUserId())
    }

    @Test
    fun signOut_success() {
        auth.signOut()
        verify(mockFirebaseAuth).signOut()
    }

    @Test
    fun createUser_success() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val completedTask: Task<AuthResult> = Tasks.forResult(null)

        mock<Task<AuthResult>> {
            on { mockFirebaseAuth.createUserWithEmailAndPassword(email, password) } doReturn completedTask
        }
        auth.createUser(email, password)

        verify(mockFirebaseAuth).createUserWithEmailAndPassword(email, password)
    }

    @Test
    fun signIn_success() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val completedTask: Task<AuthResult> = Tasks.forResult(null)

        mock<Task<AuthResult>> {
            on { mockFirebaseAuth.signInWithEmailAndPassword(email, password) } doReturn completedTask
        }
        auth.signIn(email, password)

        verify(mockFirebaseAuth).signInWithEmailAndPassword(email, password)
    }
}