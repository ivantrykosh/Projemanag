package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreBoard
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class BoardRepositoryImplTest {

    private lateinit var mockFirestoreBoard: FirestoreBoard
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var boardRepository: BoardRepositoryImpl

    @Before
    fun setUp() {
        mockFirestoreBoard = mock()
        mockFirebaseAuth = mock()
        boardRepository = BoardRepositoryImpl(mockFirestoreBoard, mockFirebaseAuth)
    }

    @Test
    fun createBoard_success() = runTest {
        val board = Board()

        whenever(mockFirestoreBoard.createBoard(board)).doReturn(Unit)
        boardRepository.createBoard(board)

        verify(mockFirestoreBoard).createBoard(board)
    }

    @Test
    fun getBoards_success() = runTest {
        val userId = "testUserId"
        val boards = listOf(Board(documentId = "testBoardId1"), Board(documentId = "testBoardId2"))

        whenever(mockFirebaseAuth.getCurrentUserId()).doReturn(userId)
        whenever(mockFirestoreBoard.getBoards(userId)).doReturn(boards)
        val retrievedBoards = boardRepository.getBoards()

        assertEquals(boards.size, retrievedBoards.size)
        assertEquals(boards[0].documentId, retrievedBoards[0].documentId)
    }

    @Test
    fun getBoard_success() = runTest {
        val boardId = "testBoardId"
        val boardName = "test board name"
        val board = Board(documentId = boardId, name = boardName)

        whenever(mockFirestoreBoard.getBoard(boardId)).doReturn(board)
        val retrievedBoard = boardRepository.getBoard(boardId)

        assertEquals(board.documentId, retrievedBoard.documentId)
        assertEquals(board.name, retrievedBoard.name)
    }

    @Test
    fun updateTasks_success() = runTest {
        val boardId = "testBoardId"
        val tasks = listOf(Task())

        whenever(mockFirestoreBoard.updateTasks(boardId, tasks)).doReturn(Unit)
        boardRepository.updateTasks(boardId, tasks)

        verify(mockFirestoreBoard).updateTasks(boardId, tasks)
    }

    @Test
    fun assignMembers_success() = runTest {
        val boardId = "testBoardId"
        val members = listOf("testUserId1")

        whenever(mockFirestoreBoard.assignMembers(boardId, members)).doReturn(Unit)
        boardRepository.assignMembers(boardId, members)

        verify(mockFirestoreBoard).assignMembers(boardId, members)
    }

    @Test
    fun deleteBoard_success() = runTest {
        val boardId = "testBoardId"

        whenever(mockFirestoreBoard.deleteBoard(boardId)).doReturn(Unit)
        boardRepository.deleteBoard(boardId)

        verify(mockFirestoreBoard).deleteBoard(boardId)
    }
}