package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AssignMembersUseCaseTest {

    private lateinit var assignMembersUseCase: AssignMembersUseCase

    @Before
    fun setup() {
        assignMembersUseCase = AssignMembersUseCase(BoardRepositoryImpl)
    }

    @Test
    fun assignMembers_success() = runBlocking {
        val boardId = "testBoardId"
        val members = listOf("testUser1Id", "testUser2Id")

        assignMembersUseCase(boardId, members).collect { result ->
            when (result) {
                is Resource.Error -> fail(result.message)
                is Resource.Loading -> { }
                is Resource.Success -> { }
            }
        }
    }

    @Test
    fun assignMembers_wrongBoardId() = runBlocking {
        val boardId = ""
        val members = listOf("testUser1Id", "testUser2Id")

        assignMembersUseCase(boardId, members).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> fail("Must be error")
            }
        }
    }

    @Test
    fun assignMembers_wrongUserId() = runBlocking {
        val boardId = "testBoardId"
        val members = listOf("testUser1Id", "")

        assignMembersUseCase(boardId, members).collect { result ->
            when (result) {
                is Resource.Error -> { }
                is Resource.Loading -> { }
                is Resource.Success -> fail("Must be error")
            }
        }
    }

    @Test(expected = CancellationException::class)
    fun assignMembers_checkFirstEmit() = runBlocking {
        val boardId = "testBoardId"
        val members = listOf("testUser1Id", "testUser2Id")

        assignMembersUseCase(boardId, members).collect { result ->
            when (result) {
                is Resource.Error -> fail("Must be loading")
                is Resource.Loading -> this.cancel()
                is Resource.Success -> fail("Must be loading")
            }
        }
    }
}