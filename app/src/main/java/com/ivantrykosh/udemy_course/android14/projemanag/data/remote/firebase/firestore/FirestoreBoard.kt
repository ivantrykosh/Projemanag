package com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirestoreCollections
import kotlinx.coroutines.tasks.await

class FirestoreBoard {
    private val mFirestore = FirebaseFirestore.getInstance()

    suspend fun createBoard(board: Board) {
        mFirestore.collection(FirestoreCollections.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .await()
    }

    suspend fun getBoards(userId: String): List<Board> {
        return mFirestore.collection(FirestoreCollections.BOARDS)
            .whereArrayContains(Board.FIELDS.ASSIGNED_TO, userId)
            .get()
            .await()
            .map {
                val board = it.toObject(Board::class.java)
                board.documentId = it.id
                board
            }
    }

    suspend fun getBoard(id: String): Board {
        return mFirestore.collection(FirestoreCollections.BOARDS)
            .document(id)
            .get()
            .await()
            .let {
                val board = it.toObject(Board::class.java)!!
                board.documentId = it.id
                board
            }
    }

    suspend fun updateTasks(id: String, tasks: List<Task>) {
        val updatedTasks = mapOf(Board.FIELDS.TASK_LIST to tasks)
        mFirestore.collection(FirestoreCollections.BOARDS)
            .document(id)
            .update(updatedTasks)
            .await()
    }

    suspend fun assignMembers(id: String, members: List<String>) {
        val assignedMembers = mapOf(Board.FIELDS.ASSIGNED_TO to members)
        mFirestore.collection(FirestoreCollections.BOARDS)
            .document(id)
            .update(assignedMembers)
            .await()
    }
}