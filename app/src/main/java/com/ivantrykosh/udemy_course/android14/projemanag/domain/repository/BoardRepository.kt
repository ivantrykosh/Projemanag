package com.ivantrykosh.udemy_course.android14.projemanag.domain.repository

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task

interface BoardRepository {

    /**
     * Create new board
     */
    suspend fun createBoard(board: Board)

    /**
     * Get boards current user assigned to
     */
    suspend fun getBoards(): List<Board>

    /**
     * Get board by its id
     */
    suspend fun getBoard(id: String): Board

    /**
     * Update tasks for board with corresponding id
     */
    suspend fun updateTasks(id: String, tasks: List<Task>)

    /**
     * Assign members to board with corresponding id
     */
    suspend fun assignMembers(id: String, members: List<String>)

    /**
     * Delete board with corresponding id
     */
    suspend fun deleteBoard(id: String)
}