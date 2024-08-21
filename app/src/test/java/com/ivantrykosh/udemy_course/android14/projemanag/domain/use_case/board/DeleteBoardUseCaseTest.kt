package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DeleteBoardUseCaseTest {

    private lateinit var deleteBoardUseCase: DeleteBoardUseCase

    @Before
    fun setup() {
        deleteBoardUseCase = DeleteBoardUseCase(BoardRepositoryImpl)
    }

    @Test
    fun createBoard_success() = runBlocking {
        val boardId = "testBoardId"

        deleteBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun createBoard_wrongBoardId() = runBlocking {
        val boardId = ""

        deleteBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun createBoard_checkFirstEmit() = runBlocking {
        val boardId = "testBoardId"

        deleteBoardUseCase(boardId).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}