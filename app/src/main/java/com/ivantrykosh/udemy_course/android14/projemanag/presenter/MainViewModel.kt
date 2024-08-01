package com.ivantrykosh.udemy_course.android14.projemanag.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.GetCurrentUserDataUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserDataUseCase: GetCurrentUserDataUseCase
) : ViewModel() {

    private val _getCurrentUserDataState = MutableLiveData(GetCurrentUserDataState())
    val getCurrentUserDataState: LiveData<GetCurrentUserDataState> = _getCurrentUserDataState

    fun getCurrentUserData() {
        getCurrentUserDataUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _getCurrentUserDataState.value = GetCurrentUserDataState(data = result.data)
                }
                is Resource.Error -> {
                    _getCurrentUserDataState.value = GetCurrentUserDataState(error = result.message)
                }
                is Resource.Loading -> {
                    _getCurrentUserDataState.value = GetCurrentUserDataState(loading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}