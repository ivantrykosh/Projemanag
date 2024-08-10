package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.AssignMembersUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetUserByEmailUseCase
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
class MembersViewModel @Inject constructor(
    private val getUsersByIdsUseCase: GetUsersByIdsUseCase,
    private val assignMembersUseCase: AssignMembersUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _getUsersByIdsState = MutableLiveData<State<List<User>>>()
    val getUsersByIdsState: LiveData<State<List<User>>> = _getUsersByIdsState

    private val _assignMembersState = MutableLiveData<State<Unit>>()
    val assignMembersState: LiveData<State<Unit>> = _assignMembersState

    private val _getUserByEmailState = MutableLiveData<State<User>>()
    val getUserByEmailState: LiveData<State<User>> = _getUserByEmailState

    fun getUsersByIds(usersIds: List<String>) {
        getUsersByIdsUseCase(usersIds).onEach { result ->
            when (result) {
                is Resource.Success -> _getUsersByIdsState.value = State(data = result.data)
                is Resource.Error -> _getUsersByIdsState.value = State(error = result.message)
                is Resource.Loading -> _getUsersByIdsState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun assignMembers(boardId: String, members: List<String>) {
        assignMembersUseCase(boardId, members).onEach { result ->
            when (result) {
                is Resource.Success -> _assignMembersState.value = State(data = result.data)
                is Resource.Error -> _assignMembersState.value = State(error = result.message)
                is Resource.Loading -> _assignMembersState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun getUserByEmail(email: String) {
        getUserByEmailUseCase(email).onEach { result ->
            when (result) {
                is Resource.Success -> _getUserByEmailState.value = State(data = result.data)
                is Resource.Error -> _getUserByEmailState.value = State(error = result.message)
                is Resource.Loading -> _getUserByEmailState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }
}