package com.ivantrykosh.udemy_course.android14.projemanag.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirebaseCollections

class Firestore {
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mFirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(userInfo: User, onSuccessListener: () -> Unit) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                Log.e("FireStore", "Error writing document")
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = mFirebaseAuth.currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun loadUserData(onSuccessListener: (User) -> Unit, onFailureListener: () -> Unit) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!
                onSuccessListener(loggedInUser)
            }
            .addOnFailureListener {
                onFailureListener()
                Log.e("FireStore", "Error reading document")
            }
    }

    fun updateUserProfileData(onSuccessListener: () -> Unit, onFailureListener: (String) -> Unit, userHashMap: HashMap<String, Any>) {
        mFirestore.collection(FirebaseCollections.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it.message.toString())
            }
    }

    fun signOut() {
        mFirebaseAuth.signOut()
    }

    fun createBoard(onSuccessListener: () -> Unit, onFailureListener: () -> Unit, boardInfo: Board) {
        mFirestore.collection(FirebaseCollections.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                Log.e("FireStore", it.message.toString())
                onFailureListener()
            }
    }

    fun getBoardsList(onSuccessListener: (List<Board>) -> Unit, onFailureListener: () -> Unit) {
        mFirestore.collection(FirebaseCollections.BOARDS)
            .whereArrayContains(com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board.FIELDS.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                val boardList = it.map { doc ->
                    val board = doc.toObject(Board::class.java)
                    board.documentId = doc.id
                    board
                }
                onSuccessListener(boardList)
            }
            .addOnFailureListener {
                onFailureListener()
            }
    }

    fun getBoardDetails(onSuccessListener: (Board) -> Unit, onFailureListener: () -> Unit, documentId: String) {
        mFirestore.collection(FirebaseCollections.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                val board = it.toObject(Board::class.java)!!
                board.documentId = it.id
                onSuccessListener(board)
            }
            .addOnFailureListener {
                onFailureListener()
            }
    }

    fun addUpdateTaskList(onSuccessListener: () -> Unit, onFailureListener: () -> Unit, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board.FIELDS.TASK_LIST] = board.taskList
        Log.e("dd", taskListHashMap.toString())
        mFirestore.collection(FirebaseCollections.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener()
            }
    }

    fun getAssignedMembersDetails(onSuccessListener: (List<User>) -> Unit, onFailureListener: () -> Unit, assignedTo: List<String>) {
        mFirestore.collection(FirebaseCollections.USERS)
            .whereIn(com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.ID, assignedTo)
            .get()
            .addOnSuccessListener { doc ->
                val usersList = doc.toObjects(User::class.java)
                onSuccessListener(usersList)
            }
            .addOnFailureListener {
                onFailureListener()
            }
    }

    fun getMemberDetails(onSuccessListener: (User) -> Unit, onFailureListener: (String) -> Unit, email: String) {
        mFirestore.collection(FirebaseCollections.USERS)
            .whereEqualTo(com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User.FIELDS.EMAIL, email)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.documents.size > 0) {
                    val user = doc.documents[0].toObject(User::class.java)!!
                    onSuccessListener(user)
                } else {
                    onFailureListener("No such member found")
                }
            }
            .addOnFailureListener {
                onFailureListener(it.message.toString())
            }
    }

    fun assignMemberToBoard(onSuccessListener: (User) -> Unit, onFailureListener: () -> Unit, board: Board, member: User) {
        val hashMap = HashMap<String, Any>()
        hashMap[com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board.FIELDS.ASSIGNED_TO] = board.assignedTo
        mFirestore.collection(FirebaseCollections.BOARDS)
            .document(board.documentId)
            .update(hashMap)
            .addOnSuccessListener {
                onSuccessListener(member)
            }
            .addOnFailureListener {
                onFailureListener()
            }
    }
}