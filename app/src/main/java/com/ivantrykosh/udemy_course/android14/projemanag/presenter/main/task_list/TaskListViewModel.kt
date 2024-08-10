package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.task_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.GetBoardUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.UpdateTasksUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetUsersByIdsUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getBoardUseCase: GetBoardUseCase,
    private val updateTasksUseCase: UpdateTasksUseCase,
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
): BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _getBoardState = MutableLiveData<State<Board>>()
    val getBoardState: LiveData<State<Board>> = _getBoardState

    private val _updateTasksState = MutableLiveData<State<Unit>?>()
    val updateTasksState: LiveData<State<Unit>?> = _updateTasksState

    private val _getUsersByIdsState = MutableLiveData<State<List<User>>>()
    val getUsersByIdsState: LiveData<State<List<User>>> = _getUsersByIdsState

    fun getBoard(id: String) {
        getBoardUseCase(id).onEach { result ->
            when (result) {
                is Resource.Success -> _getBoardState.value = State(data = result.data)
                is Resource.Error -> _getBoardState.value = State(error = result.message)
                is Resource.Loading -> _getBoardState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun updateTasks(boardId: String, tasks: List<Task>) {
        updateTasksUseCase(boardId, tasks).onEach { result ->
            when (result) {
                is Resource.Success -> _updateTasksState.value = State()
                is Resource.Error -> _updateTasksState.value = State(error = result.message)
                is Resource.Loading -> _updateTasksState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun getUsersByIds(ids: List<String>) {
        getUsersByIdsUseCase(ids).onEach { result ->
            when (result) {
                is Resource.Success -> _getUsersByIdsState.value = State(data = result.data)
                is Resource.Error -> _getUsersByIdsState.value = State(error = result.message)
                is Resource.Loading -> _getUsersByIdsState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun clearValues() {
        _updateTasksState.value = null
    }
}