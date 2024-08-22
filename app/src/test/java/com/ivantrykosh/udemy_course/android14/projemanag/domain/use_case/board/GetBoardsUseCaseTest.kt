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
class GetBoardsUseCaseTest {

    private lateinit var getBoardsUseCase: GetBoardsUseCase

    @Before
    fun setup() {
        getBoardsUseCase = GetBoardsUseCase(BoardRepositoryImpl)
    }

    @Test
    fun getBoards_success() = runBlocking {
        var boards: List<Board> = emptyList()
        getBoardsUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { boards = result.data!! }
            }
        }

        assertEquals(BoardRepositoryImpl.listOfBoards.size, boards.size)
        assertEquals(BoardRepositoryImpl.listOfBoards[0].documentId, boards.first().documentId)
    }

    @Test(expected = CancellationException::class)
    fun getBoards_checkFirstEmit() = runBlocking {
        getBoardsUseCase().collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}