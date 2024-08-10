package com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.SignInUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _signInState = MutableLiveData<State<Unit>>()
    val signInState: LiveData<State<Unit>> = _signInState

    fun signIn(email: String, password: String) {
        signInUseCase(email, password).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _signInState.value = State()
                }
                is Resource.Error -> {
                    _signInState.value = State(error = result.message)
                }
                is Resource.Loading -> {
                    _signInState.value = State(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}