package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.my_profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage.UploadImageUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.UpdateUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase) {

    private val _updateUserState = MutableLiveData<State<Unit>>()
    val updateUserState: LiveData<State<Unit>> = _updateUserState

    private val _uploadImageState = MutableLiveData<State<String>>()
    val uploadImageState: LiveData<State<String>> = _uploadImageState

    fun updateUser(newImageUrl: String = "", newName: String = "", newMobile: Long = 0) {
        val userData = getUserDataMapFromValues(newImageUrl, newName, newMobile)
        updateUserUseCase(userData).onEach { result ->
            when (result) {
                is Resource.Success -> _updateUserState.value = State()
                is Resource.Error -> _updateUserState.value = State(error = result.message)
                is Resource.Loading -> _updateUserState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    fun uploadImage(newName: String, imageUri: Uri) {
        uploadImageUseCase(newName, imageUri).onEach { result ->
            when (result) {
                is Resource.Success -> _uploadImageState.value = State(data = result.data)
                is Resource.Error -> _uploadImageState.value = State(error = result.message)
                is Resource.Loading -> _uploadImageState.value = State(loading = true)
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserDataMapFromValues(newImageUrl: String, newName: String, newMobile: Long): Map<String, Any> {
        val userMap = mutableMapOf<String, Any>()
        if (newImageUrl.isNotEmpty()) {
            userMap[User.FIELDS.IMAGE] = newImageUrl
        }
        if (newName.isNotEmpty()) {
            userMap[User.FIELDS.NAME] = newName
        }
        if (newMobile != 0L) {
            userMap[User.FIELDS.MOBILE] = newMobile
        }
        return userMap
    }
}