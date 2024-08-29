package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.card_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.UpdateTasksUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
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
class CardDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var updateTasksUseCase: UpdateTasksUseCase

    private lateinit var cardDetailsViewModel: CardDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        cardDetailsViewModel = CardDetailsViewModel(updateTasksUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun updateTasks_emitSuccessState() = runTest {
        val boardId = "testBoardId"
        val tasks = listOf(Task("Test Title", "testUserId"))
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        cardDetailsViewModel.updateTasksState.observeForever(observer)

        cardDetailsViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateTasks_emitErrorState() = runTest {
        val boardId = ""
        val tasks = listOf(Task("Test Title", "testUserId"))
        val errorMessage = "Failed to update tasks"
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        cardDetailsViewModel.updateTasksState.observeForever(observer)

        cardDetailsViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateTasks_emitLoadingState() = runTest {
        val boardId = "testBoardId"
        val tasks = listOf(Task("Test Title", "testUserId"))
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        cardDetailsViewModel.updateTasksState.observeForever(observer)

        cardDetailsViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}