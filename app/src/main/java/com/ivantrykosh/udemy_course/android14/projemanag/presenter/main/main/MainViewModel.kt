package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.GetBoardsUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.UpdateUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.SignOutUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBoardsUseCase: GetBoardsUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
): BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _getBoardsState = MutableLiveData<State<List<Board>>>()
    val getBoardsState: LiveData<State<List<Board>>> = _getBoardsState

    private val _updateUserFCMTokenState = MutableLiveData<State<Unit>>()
    val updateUserFCMTokenState: LiveData<State<Unit>> = _updateUserFCMTokenState

    private val _signOutState = MutableLiveData<State<Unit>>()
    val signOutState: LiveData<State<Unit>> = _signOutState

    fun getBoards() {
        _getBoardsState.value = State(loading = true)
        getBoardsUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> _getBoardsState.value = State(data = result.data)
                is Resource.Error -> _getBoardsState.value = State(error = result.message)
                is Resource.Loading -> _getBoardsState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun updateUserFCMToken(newToken: String) {
        val userHashMap = mapOf(User.FIELDS.FCM_TOKEN to newToken)
        _updateUserFCMTokenState.value = State(loading = true)
        updateUserUseCase(userHashMap).onEach { result ->
            when (result) {
                is Resource.Success -> _updateUserFCMTokenState.value = State()
                is Resource.Error -> _updateUserFCMTokenState.value = State(error = result.message)
                is Resource.Loading -> _updateUserFCMTokenState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun signOut() {
        _signOutState.value = State(loading = true)
        signOutUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    AppPreferences.clear()
                    _signOutState.value = State()
                }
                is Resource.Error -> {
                    _signOutState.value = State(error = result.message)
                }
                is Resource.Loading -> {
                    _signOutState.value = State(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}