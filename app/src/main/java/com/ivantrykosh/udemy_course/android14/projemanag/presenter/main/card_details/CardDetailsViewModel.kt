package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.card_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.UpdateTasksUseCase
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
class CardDetailsViewModel @Inject constructor(
    private val updateTasksUseCase: UpdateTasksUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _updateTasksState = MutableLiveData<State<Unit>>()
    val updateTasksState: LiveData<State<Unit>> = _updateTasksState

    fun updateTasks(boardId: String, tasks: List<Task>) {
        updateTasksUseCase(boardId, tasks).onEach { result ->
            when (result) {
                is Resource.Success -> _updateTasksState.value = State()
                is Resource.Error -> _updateTasksState.value = State(error = result.message)
                is Resource.Loading -> _updateTasksState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }
}