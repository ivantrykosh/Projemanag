package com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.sign_in

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.SignInUseCase
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
class SignInViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var signInUseCase: SignInUseCase

    private lateinit var signInViewModel: SignInViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        signInViewModel = SignInViewModel(signInUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun signIn_emitSuccessState() = runTest {
        val email = "test@email.com"
        val password = "p@SSw0rd"
        whenever(signInUseCase(email, password)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        signInViewModel.signInState.observeForever(observer)

        signInViewModel.signIn(email, password)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun signIn_emitErrorState() = runTest {
        val email = "test@email.com"
        val password = ""
        val errorMessage = "Failed to sign in"
        whenever(signInUseCase(email, password)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        signInViewModel.signInState.observeForever(observer)

        signInViewModel.signIn(email, password)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun signIn_emitLoadingState() = runTest {
        val email = "test@email.com"
        val password = "p@SSw0rd"
        whenever(signInUseCase(email, password)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        signInViewModel.signInState.observeForever(observer)

        signInViewModel.signIn(email, password)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}