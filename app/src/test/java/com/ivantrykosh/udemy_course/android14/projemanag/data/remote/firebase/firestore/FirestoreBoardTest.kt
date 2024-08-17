package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirestoreCollections
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class FirestoreBoardTest {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var firestoreBoard: FirestoreBoard

    @Before
    fun setup() {
        collectionReference = mock()
        mockFirestore = mock {
            on { it.collection(FirestoreCollections.BOARDS) } doReturn collectionReference
        }
        firestoreBoard = FirestoreBoard(mockFirestore)
    }

    @Test
    fun createBoard() = runTest {
        val board = Board()

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document() } doReturn it
            on { it.set(board, SetOptions.merge()) } doReturn completedTask
        }

        firestoreBoard.createBoard(board)
        verify(document).set(board, SetOptions.merge())
    }
}