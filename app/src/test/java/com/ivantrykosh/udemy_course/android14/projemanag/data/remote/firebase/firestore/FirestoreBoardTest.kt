package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
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
    fun createBoard_success() = runTest {
        val board = Board()

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document() } doReturn it
            on { it.set(board, SetOptions.merge()) } doReturn completedTask
        }

        firestoreBoard.createBoard(board)
        verify(document).set(board, SetOptions.merge())
    }

    @Test
    fun getBoards_notEmpty_success() = runTest {
        val userId = "testUserId"
        val boardId = "testBoardId"

        val queryDocumentSnapshot = mock<QueryDocumentSnapshot> {
            on { it.toObject(Board::class.java) } doReturn Board()
            on { it.id } doReturn boardId
        }
        val querySnapshot = mock<QuerySnapshot> {
            on { it.iterator() } doReturn mutableListOf(queryDocumentSnapshot).iterator()
        }
        val completedTask = Tasks.forResult(querySnapshot)
        mock<Query> {
            on { collectionReference.whereArrayContains(Board.FIELDS.ASSIGNED_TO, userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val boards = firestoreBoard.getBoards(userId)
        assertEquals(1, boards.size)
        assertEquals(boardId, boards[0].documentId)
    }

    @Test
    fun getBoards_empty_success() = runTest {
        val userId = "testUserId"

        val querySnapshot = mock<QuerySnapshot> {
            on { it.iterator() } doReturn mutableListOf<QueryDocumentSnapshot>().iterator()
        }
        val completedTask = Tasks.forResult(querySnapshot)
        mock<Query> {
            on { collectionReference.whereArrayContains(Board.FIELDS.ASSIGNED_TO, userId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val boards = firestoreBoard.getBoards(userId)
        assertEquals(0, boards.size)
    }

    @Test
    fun getBoard_success() = runTest {
        val boardId = "testBoardId"

        val documentSnapshot = mock<DocumentSnapshot> {
            onGeneric { it.toObject(Board::class.java)!! } doReturn Board()
            on { it.id } doReturn boardId
        }
        val completedTask = Tasks.forResult(documentSnapshot)
        mock<DocumentReference> {
            on { collectionReference.document(boardId) } doReturn it
            on { it.get() } doReturn completedTask
        }

        val board = firestoreBoard.getBoard(boardId)
        assertEquals(boardId, board.documentId)
    }

    @Test
    fun updateTasks_success() = runTest {
        val boardId = "testBoardId"
        val tasks = listOf(Task())
        val updatedTasks = mapOf(Board.FIELDS.TASK_LIST to tasks)

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document(boardId) } doReturn it
            on { it.update(updatedTasks) } doReturn completedTask
        }

        firestoreBoard.updateTasks(boardId, tasks)
        verify(document).update(updatedTasks)
    }

    @Test
    fun assignMembers_success() = runTest {
        val boardId = "testBoardId"
        val members = listOf("testUserId")
        val assignedMembers = mapOf(Board.FIELDS.ASSIGNED_TO to members)

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document(boardId) } doReturn it
            on { it.update(assignedMembers) } doReturn completedTask
        }

        firestoreBoard.assignMembers(boardId, members)
        verify(document).update(assignedMembers)
    }

    @Test
    fun deleteBoard_success() = runTest {
        val boardId = "testBoardId"

        val completedTask = Tasks.forResult<Void>(null)
        val document = mock<DocumentReference> {
            on { collectionReference.document(boardId) } doReturn it
            on { it.delete() } doReturn completedTask
        }

        firestoreBoard.deleteBoard(boardId)
        verify(document).delete()
    }
}