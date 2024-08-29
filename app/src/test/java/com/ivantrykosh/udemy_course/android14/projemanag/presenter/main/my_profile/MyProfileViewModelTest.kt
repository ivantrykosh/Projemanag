package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.my_profile

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage.UploadImageUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.UpdateUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MyProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Mock
    private lateinit var uploadImageUseCase: UploadImageUseCase

    private lateinit var myProfileViewModel: MyProfileViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        myProfileViewModel = MyProfileViewModel(updateUserUseCase, uploadImageUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun updateUser_emitSuccessState() = runTest {
        val newImageUrl = "newImageUrl"
        val newName = "New Name"
        val newMobile = 123311L
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl, User.FIELDS.NAME to newName, User.FIELDS.MOBILE to newMobile)
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl, newName, newMobile)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUser_emitErrorState() = runTest {
        val newImageUrl = "newImageUrl"
        val newName = "New Name"
        val newMobile = 123311L
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl, User.FIELDS.NAME to newName, User.FIELDS.MOBILE to newMobile)
        val errorMessage = "Failed to update user"
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl, newName, newMobile)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUser_emitLoadingState() = runTest {
        val newImageUrl = "newImageUrl"
        val newName = "New Name"
        val newMobile = 123311L
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl, User.FIELDS.NAME to newName, User.FIELDS.MOBILE to newMobile)
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl, newName, newMobile)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUser_oneParameter_emitSuccessState() = runTest {
        val newImageUrl = "newImageUrl"
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl)
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUser_oneParameter_emitErrorState() = runTest {
        val newImageUrl = "newImageUrl"
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl)
        val errorMessage = "Failed to update user"
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUser_oneParameter_emitLoadingState() = runTest {
        val newImageUrl = "newImageUrl"
        val userData = mapOf<String, Any>(User.FIELDS.IMAGE to newImageUrl)
        whenever(updateUserUseCase(userData)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        myProfileViewModel.updateUserState.observeForever(observer)

        myProfileViewModel.updateUser(newImageUrl)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun uploadImage_emitSuccessState() = runTest {
        val newName = "New Image Name"
        val imageUri = mock<Uri>()
        val downloadUrl = "imageUrl"
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Success(downloadUrl)))

        val observer = mock<Observer<State<String>>>()
        myProfileViewModel.uploadImageState.observeForever(observer)

        myProfileViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(data = downloadUrl))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun uploadImage_emitErrorState() = runTest {
        val newName = ""
        val imageUri = mock<Uri>()
        val errorMessage = "Failed to upload image"
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<String>>>()
        myProfileViewModel.uploadImageState.observeForever(observer)

        myProfileViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun uploadImage_emitLoadingState() = runTest {
        val newName = "New Image Name"
        val imageUri = mock<Uri>()
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<String>>>()
        myProfileViewModel.uploadImageState.observeForever(observer)

        myProfileViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}