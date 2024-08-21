package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository

object BoardRepositoryImpl : BoardRepository {

    override suspend fun createBoard(board: Board) {
        if (board.createdBy.isEmpty()) {
            throw Exception("ID of user that created the board is incorrect")
        }
    }

    override suspend fun getBoards(): List<Board> {
        TODO("Not yet implemented")
    }

    override suspend fun getBoard(id: String): Board {
        TODO("Not yet implemented")
    }

    override suspend fun updateTasks(id: String, tasks: List<Task>) {
        TODO("Not yet implemented")
    }

    override suspend fun assignMembers(id: String, members: List<String>) {
        if (id.isEmpty()) {
            throw Exception("ID of board is incorrect")
        } else {
            members.forEach {
                if (it.isEmpty()) {
                    throw Exception("ID of user is incorrect")
                }
            }
        }
    }

    override suspend fun deleteBoard(id: String) {
        if (id.isEmpty()) {
            throw Exception("ID of board is incorrect")
        }
    }
}