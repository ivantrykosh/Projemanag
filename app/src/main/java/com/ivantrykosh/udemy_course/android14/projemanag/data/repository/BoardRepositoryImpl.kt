package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseAuth
import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore.FirestoreBoard
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val firestoreBoard: FirestoreBoard,
    private val firebaseAuth: FirebaseAuth,
) : BoardRepository {

    override suspend fun createBoard(board: Board) {
        firestoreBoard.createBoard(board)
    }

    override suspend fun getBoards(): List<Board> {
        val userId = firebaseAuth.getCurrentUserId()
        return firestoreBoard.getBoards(userId)
    }

    override suspend fun getBoard(id: String): Board {
        return firestoreBoard.getBoard(id)
    }

    override suspend fun updateTasks(id: String, tasks: List<Task>) {
        firestoreBoard.updateTasks(id, tasks)
    }

    override suspend fun assignMembers(id: String, members: List<String>) {
        firestoreBoard.assignMembers(id, members)
    }

    override suspend fun deleteBoard(id: String) {
        firestoreBoard.deleteBoard(id)
    }
}