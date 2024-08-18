package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirestoreCollections
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreUserTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var firestoreUser: FirestoreUser

    @Before
    fun setup() {
        collectionReference = mock()
        mockFirestore = mock {
            on { it.collection(FirestoreCollections.USERS) } doReturn collectionReference
        }
        firestoreUser = FirestoreUser(mockFirestore)
    }

    @Test
    fun createUser_success() = runTest {
        val user = User()
        val userId = "testUserId"

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document(userId) } doReturn it
            on { it.set(user, SetOptions.merge()) } doReturn completedTask
        }

        firestoreUser.createUser(user, userId)
        verify(document).set(user, SetOptions.merge())
    }

    @Test
    fun getUserById_success() = runTest {
        val userId = "testUserId"

        val documentSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(User::class.java)!! } doReturn User(id = userId)
        }
        val completedTask = Tasks.forResult(documentSnapshot)
        mock<DocumentReference> {
            on { collectionReference.document(userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val user = firestoreUser.getUserById(userId)
        assertEquals(userId, user.id)
    }

    @Test
    fun updateUser_success() = runTest {
        val userId = "testUserId"
        val userData = mock<Map<String, Any>>()

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document(userId) } doReturn it
            on { it.update(userData) } doReturn completedTask
        }

        firestoreUser.updateUser(userId, userData)
        verify(document).update(userData)
    }

    @Test
    fun getUserByEmail_success() = runTest {
        val email = "test@email.com"

        val querySnapshot = mock<QuerySnapshot> {
            on { it.toObjects(User::class.java) } doReturn listOf(User(email = email))
        }
        val completedTask = Tasks.forResult(querySnapshot)
        mock<Query> {
            on { collectionReference.whereEqualTo(User.FIELDS.EMAIL, email) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val user = firestoreUser.getUserByEmail(email)
        assertEquals(email, user.email)
    }

    @Test
    fun getUsersByIds_success() = runTest {
        val userIds = listOf("testUserId1", "testUserId2")

        val querySnapshot = mock<QuerySnapshot> {
            on { it.toObjects(User::class.java) } doReturn listOf(User(id = userIds[0]), User(id = userIds[1]))
        }
        val completedTask = Tasks.forResult(querySnapshot)
        mock<Query> {
            on { collectionReference.whereIn(User.FIELDS.ID, userIds) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val users = firestoreUser.getUsersByIds(userIds)
        assertEquals(userIds.size, users.size)
        assertEquals(userIds[0], users[0].id)
    }
}