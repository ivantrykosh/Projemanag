package com.ivantrykosh.udemy_course.android14.projemanag.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _getCurrentUserState = MutableLiveData(GetCurrentUserState())
    val getCurrentUserState: LiveData<GetCurrentUserState> = _getCurrentUserState

    fun getCurrentUser() {
        getCurrentUserUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _getCurrentUserState.value = GetCurrentUserState(data = result.data)
                }
                is Resource.Error -> {
                    _getCurrentUserState.value = GetCurrentUserState(error = result.message)
                }
                is Resource.Loading -> {
                    _getCurrentUserState.value = GetCurrentUserState(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}