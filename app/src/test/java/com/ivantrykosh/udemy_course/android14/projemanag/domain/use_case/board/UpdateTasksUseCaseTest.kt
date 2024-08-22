package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
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
class UpdateTasksUseCaseTest {

    private lateinit var updateTasksUseCase: UpdateTasksUseCase

    @Before
    fun setup() {
        updateTasksUseCase = UpdateTasksUseCase(BoardRepositoryImpl)
    }

    @Test
    fun updateTasks_success() = runBlocking {
        val boardId = "testBoardId"
        val tasks = listOf(Task(createdBy = "testUser1Id"), Task(createdBy = "testUser2Id"))

        updateTasksUseCase(boardId, tasks).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun updateTasks_wrongBoardId() = runBlocking {
        val boardId = ""
        val tasks = listOf(Task(createdBy = "testUser1Id"), Task(createdBy = "testUser2Id"))

        updateTasksUseCase(boardId, tasks).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test
    fun updateTasks_wrongUserId() = runBlocking {
        val boardId = "testBoardId"
        val tasks = listOf(Task(createdBy = "testUser1Id"), Task())

        updateTasksUseCase(boardId, tasks).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> Assert.fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun updateTasks_checkFirstEmit() = runBlocking {
        val boardId = "testBoardId"
        val tasks = listOf(Task(createdBy = "testUser1Id"), Task(createdBy = "testUser2Id"))

        updateTasksUseCase(boardId, tasks).collect { result ->
            when (result) {
                is Resource.Error -> Assert.fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> Assert.fail("Must be loading")
            }
        }
    }
}