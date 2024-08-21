package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CreateBoardUseCaseTest {

    private lateinit var createBoardUseCase: CreateBoardUseCase

    @Before
    fun setup() {
        createBoardUseCase = CreateBoardUseCase(BoardRepositoryImpl)
    }

    @Test
    fun createBoard_success() = runBlocking {
        val board = Board(createdBy = "testUserId")

        createBoardUseCase(board).collect { result ->
            when (result) {
                is Resource.Error -> fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun createBoard_wrongUserId() = runBlocking {
        val board = Board()

        createBoardUseCase(board).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun createBoard_checkFirstEmit() = runBlocking {
        val board = Board(createdBy = "testUserId")

        createBoardUseCase(board).collect { result ->
            when (result) {
                is Resource.Error -> fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> fail("Must be loading")
            }
        }
    }
}