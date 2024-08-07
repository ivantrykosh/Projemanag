package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.create_board

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.CreateBoardUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage.UploadImageUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val uploadImageUseCase: UploadImageUseCase,
    private val createBoardUseCase: CreateBoardUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _uploadImageState = MutableLiveData<State<String>>()
    val uploadImageState: LiveData<State<String>> = _uploadImageState

    private val _createBoardState = MutableLiveData<State<String>>()
    val createBoardState: LiveData<State<String>> = _createBoardState

    fun uploadImage(newName: String, imageUri: Uri) {
        _uploadImageState.value = State(loading = true)
        uploadImageUseCase(newName, imageUri).onEach { result ->
            when (result) {
                is Resource.Success -> _uploadImageState.value = State(data = result.data)
                is Resource.Error -> _uploadImageState.value = State(error = result.message)
                is Resource.Loading -> _uploadImageState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun createBoard(name: String, imageUrl: String, createdBy: String, assignedTo: ArrayList<String>) {
        val newBoard = Board(
            name = name,
            image = imageUrl,
            createdBy = createdBy,
            assignedTo = assignedTo,
        )
        _createBoardState.value = State(loading = true)
        createBoardUseCase(newBoard).onEach { result ->
            when (result) {
                is Resource.Success -> _createBoardState.value = State()
                is Resource.Error -> _createBoardState.value = State(error = result.message)
                is Resource.Loading -> _createBoardState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }
}