package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.main

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.DeleteBoardUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.GetBoardsUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_messaging.GetTokenUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.UpdateUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.GetCurrentUserIdUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth.SignOutUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.State
import com.ivantrykosh.udemy_course.android14.projemanag.utils.AppPreferences
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
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var getBoardsUseCase: GetBoardsUseCase

    @Mock
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @Mock
    private lateinit var signOutUseCase: SignOutUseCase

    @Mock
    private lateinit var getTokenUseCase: GetTokenUseCase

    @Mock
    private lateinit var deleteBoardUseCase: DeleteBoardUseCase

    private lateinit var mainViewModel: MainViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        mainViewModel = MainViewModel(getBoardsUseCase, updateUserUseCase, signOutUseCase, getTokenUseCase, deleteBoardUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun getBoards_emitSuccessState() = runTest {
        val boards = listOf(Board("boardId1", "Test Board", "image.jpg", "testUserId"))
        whenever(getBoardsUseCase()).thenReturn(flowOf(Resource.Success(boards)))

        val observer = mock<Observer<State<List<Board>>>>()
        mainViewModel.getBoardsState.observeForever(observer)

        mainViewModel.getBoards()

        verify(observer).onChanged(State(data = boards))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getBoards_emitErrorState() = runTest {
        val errorMessage = "Failed to get boards"
        whenever(getBoardsUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<List<Board>>>>()
        mainViewModel.getBoardsState.observeForever(observer)

        mainViewModel.getBoards()

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getBoards_emitLoadingState() = runTest {
        whenever(getBoardsUseCase()).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<List<Board>>>>()
        mainViewModel.getBoardsState.observeForever(observer)

        mainViewModel.getBoards()

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUserFCMToken_emitSuccessState() = runTest {
        val newToken = "newToken"
        val userHashMap = mapOf(User.FIELDS.FCM_TOKEN to newToken)
        whenever(updateUserUseCase(userHashMap)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.updateUserFCMTokenState.observeForever(observer)

        mainViewModel.updateUserFCMToken(newToken)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUserFCMToken_emitErrorState() = runTest {
        val newToken = "newToken"
        val userHashMap = mapOf(User.FIELDS.FCM_TOKEN to newToken)
        val errorMessage = "Failed to update user fcm token"
        whenever(updateUserUseCase(userHashMap)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.updateUserFCMTokenState.observeForever(observer)

        mainViewModel.updateUserFCMToken(newToken)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateUserFCMToken_emitLoadingState() = runTest {
        val newToken = "newToken"
        val userHashMap = mapOf(User.FIELDS.FCM_TOKEN to newToken)
        whenever(updateUserUseCase(userHashMap)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.updateUserFCMTokenState.observeForever(observer)

        mainViewModel.updateUserFCMToken(newToken)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun signOut_emitSuccessState() = runTest {
        val editor = mock<Editor> {
            on { it.clear() } doReturn it
            doNothing().whenever(it).apply()
        }
        val sharedPrefs = mock<SharedPreferences> {
            on { it.edit() } doReturn editor
        }
        val context = mock<Context> {
            on { it.getSharedPreferences(any(), any()) } doReturn sharedPrefs
        }
        AppPreferences.setup(context)
        whenever(signOutUseCase()).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.signOutState.observeForever(observer)

        mainViewModel.signOut()

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun signOut_emitErrorState() = runTest {
        val errorMessage = "Failed to sign out"
        whenever(signOutUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.signOutState.observeForever(observer)

        mainViewModel.signOut()

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun signOut_emitLoadingState() = runTest {
        whenever(signOutUseCase()).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.signOutState.observeForever(observer)

        mainViewModel.signOut()

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getToken_emitSuccessState() = runTest {
        val token = "testToken"
        whenever(getTokenUseCase()).thenReturn(flowOf(Resource.Success(token)))

        val observer = mock<Observer<State<String>>>()
        mainViewModel.getTokenState.observeForever(observer)

        mainViewModel.getToken()

        verify(observer).onChanged(State(data = token))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getToken_emitErrorState() = runTest {
        val errorMessage = "Failed to get token"
        whenever(getTokenUseCase()).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<String>>>()
        mainViewModel.getTokenState.observeForever(observer)

        mainViewModel.getToken()

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getToken_emitLoadingState() = runTest {
        whenever(getTokenUseCase()).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<String>>>()
        mainViewModel.getTokenState.observeForever(observer)

        mainViewModel.getToken()

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun deleteBoard_emitSuccessState() = runTest {
        val boardId = "boardId"
        whenever(deleteBoardUseCase(boardId)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.deleteBoardState.observeForever(observer)

        mainViewModel.deleteBoard(boardId)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun deleteBoard_emitErrorState() = runTest {
        val boardId = "boardId"
        val errorMessage = "Failed to delete board"
        whenever(deleteBoardUseCase(boardId)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.deleteBoardState.observeForever(observer)

        mainViewModel.deleteBoard(boardId)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun deleteBoard_emitLoadingState() = runTest {
        val boardId = "boardId"
        whenever(deleteBoardUseCase(boardId)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        mainViewModel.deleteBoardState.observeForever(observer)

        mainViewModel.deleteBoard(boardId)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}