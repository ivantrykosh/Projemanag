package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
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
class GetBoardUseCaseTest {

    private lateinit var getBoardUseCase: GetBoardUseCase

    @Before
    fun setup() {
        getBoardUseCase = GetBoardUseCase(BoardRepositoryImpl)
    }

    @Test
    fun getBoard_success() = runBlocking {
        val boardId = BoardRepositoryImpl.testBoardId
        var board = Board()

        getBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { board = result.data!! }
            }
        }

        assertEquals(BoardRepositoryImpl.testBoardId, board.documentId)
    }

    @Test
    fun getBoard_wrongBoardId() = runBlocking {
        val boardId = "wrongId"

        getBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun getBoard_checkFirstEmit() = runBlocking {
        val boardId = BoardRepositoryImpl.testBoardId

        getBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}