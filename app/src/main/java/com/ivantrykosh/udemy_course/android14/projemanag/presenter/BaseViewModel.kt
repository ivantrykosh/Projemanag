package com.ivantrykosh.udemy_course.android14.projemanag.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : ViewModel() {

    private val _getCurrentUserState = MutableLiveData(State<User>())
    val getCurrentUserState: LiveData<State<User>> = _getCurrentUserState

    private val _getCurrentUserIdState = MutableLiveData(State<String>())
    val getCurrentUserIdState: LiveData<State<String>> = _getCurrentUserIdState

    fun getCurrentUser() {
        _getCurrentUserState.value = State(loading = true)
        getCurrentUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _getCurrentUserState.value = State(data = result.data)
                }
                is Resource.Error -> {
                    _getCurrentUserState.value = State(error = result.message)
                }
                is Resource.Loading -> {
                    _getCurrentUserState.value = State(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCurrentUserId() {
        _getCurrentUserIdState.value = State(loading = true)
        getCurrentUserIdUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _getCurrentUserIdState.value = State(data = result.data ?: "")
                }
                is Resource.Error -> {
                    _getCurrentUserIdState.value = State(error = result.message)
                }
                is Resource.Loading -> {
                    _getCurrentUserIdState.value = State(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}