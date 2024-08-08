package com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.CreateUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: CreateUserUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _signUpState = MutableLiveData<State<Unit>>()
    val signUpState: LiveData<State<Unit>> = _signUpState

    fun signUp(email: String, password: String, name: String) {
        _signUpState.value = State(loading = true)
        signUpUseCase(email, password, name).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _signUpState.value = State()
                }
                is Resource.Error -> {
                    _signUpState.value = State(error = result.message)
                }
                is Resource.Loading -> {
                    _signUpState.value = State(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}