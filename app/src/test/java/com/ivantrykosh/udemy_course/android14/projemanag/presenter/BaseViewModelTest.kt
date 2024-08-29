package com.ivantrykosh.udemy_course.android14.projemanag.presenter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
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
import org.mockito.kotlin.whenever
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class BaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    private lateinit var baseViewModel: BaseViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        baseViewModel = BaseViewModel(getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun getCurrentUser_emitSuccessState() = runTest {
        val user = User("userId", "Test User", "test@email.com")
        whenever(getCurrentUserUseCase()).thenReturn(flowOf(Resource.Success(user)))

        val observer = mock<Observer<State<User>>>()
        baseViewModel.getCurrentUserState.observeForever(observer)

        baseViewModel.getCurrentUser()

        verify(observer).onChanged(State(data = user))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getCurrentUser_emitErrorState() = runTest {
        val errorMessage = "Failed to load user"
        whenever(getCurrentUserUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<User>>>()
        baseViewModel.getCurrentUserState.observeForever(observer)

        baseViewModel.getCurrentUser()

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getCurrentUser_emitLoadingState() = runTest {
        whenever(getCurrentUserUseCase()).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<User>>>()
        baseViewModel.getCurrentUserState.observeForever(observer)

        baseViewModel.getCurrentUser()

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getCurrentUserId_emitSuccessState() = runTest {
        val userId = "testUserId"
        whenever(getCurrentUserIdUseCase()).thenReturn(flowOf(Resource.Success(userId)))

        val observer = mock<Observer<State<String>>>()
        baseViewModel.getCurrentUserIdState.observeForever(observer)

        baseViewModel.getCurrentUserId()

        verify(observer).onChanged(State(data = userId))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getCurrentUserId_emitErrorState() = runTest {
        val errorMessage = "Failed to load user ID"
        whenever(getCurrentUserIdUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<String>>>()
        baseViewModel.getCurrentUserIdState.observeForever(observer)

        baseViewModel.getCurrentUserId()

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getCurrentUserId_emitLoadingState() = runTest {
        whenever(getCurrentUserIdUseCase()).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<String>>>()
        baseViewModel.getCurrentUserIdState.observeForever(observer)

        baseViewModel.getCurrentUserId()

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}