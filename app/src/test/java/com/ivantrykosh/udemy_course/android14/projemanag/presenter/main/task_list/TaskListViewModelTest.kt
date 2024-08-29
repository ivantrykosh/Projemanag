package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.task_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Task
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.GetBoardUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.UpdateTasksUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetUsersByIdsUseCase
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
import org.junit.Assert.assertEquals
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
class TaskListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var getBoardUseCase: GetBoardUseCase

    @Mock
    private lateinit var updateTasksUseCase: UpdateTasksUseCase

    @Mock
    private lateinit var getUsersByIdsUseCase: GetUsersByIdsUseCase

    private lateinit var taskListViewModel: TaskListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        taskListViewModel = TaskListViewModel(getBoardUseCase, updateTasksUseCase, getUsersByIdsUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun getBoard_emitSuccessState() = runTest {
        val boardId = "boardId"
        val board = Board(boardId, "Board Name", "image.jpg", "testUserId")
        whenever(getBoardUseCase(boardId)).thenReturn(flowOf(Resource.Success(board)))

        val observer = mock<Observer<State<Board>>>()
        taskListViewModel.getBoardState.observeForever(observer)

        taskListViewModel.getBoard(boardId)

        verify(observer).onChanged(State(data = board))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getBoard_emitErrorState() = runTest {
        val boardId = "boardId"
        val errorMessage = "Failed to get board"
        whenever(getBoardUseCase(boardId)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Board>>>()
        taskListViewModel.getBoardState.observeForever(observer)

        taskListViewModel.getBoard(boardId)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getBoard_emitLoadingState() = runTest {
        val boardId = "boardId"
        whenever(getBoardUseCase(boardId)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Board>>>()
        taskListViewModel.getBoardState.observeForever(observer)

        taskListViewModel.getBoard(boardId)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateTasks_emitSuccessState() = runTest {
        val boardId = "boardId"
        val tasks = listOf(Task("Task Title", "testUserId"))
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>?>>()
        taskListViewModel.updateTasksState.observeForever(observer)

        taskListViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateTasks_emitErrorState() = runTest {
        val boardId = "boardId"
        val tasks = listOf(Task("Task Title", "testUserId"))
        val errorMessage = "Failed to update tasks"
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>?>>()
        taskListViewModel.updateTasksState.observeForever(observer)

        taskListViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun updateTasks_emitLoadingState() = runTest {
        val boardId = "boardId"
        val tasks = listOf(Task("Task Title", "testUserId"))
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>?>>()
        taskListViewModel.updateTasksState.observeForever(observer)

        taskListViewModel.updateTasks(boardId, tasks)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUsersByIds_emitSuccessState() = runTest {
        val usersIds = listOf("testUserId")
        val users = listOf(User("testUserId", "Test Name", "test@email.com"))
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Success(users)))

        val observer = mock<Observer<State<List<User>>>>()
        taskListViewModel.getUsersByIdsState.observeForever(observer)

        taskListViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(data = users))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUsersByIds_emitErrorState() = runTest {
        val usersIds = listOf("")
        val errorMessage = "Failed to get users by ids"
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<List<User>>>>()
        taskListViewModel.getUsersByIdsState.observeForever(observer)

        taskListViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUsersByIds_emitLoadingState() = runTest {
        val usersIds = listOf("testUserId")
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<List<User>>>>()
        taskListViewModel.getUsersByIdsState.observeForever(observer)

        taskListViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun clearValues_success() {
        val boardId = "boardId"
        val tasks = listOf<Task>()
        val errorMessage = "Failed to update tasks"
        whenever(updateTasksUseCase(boardId, tasks)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>?>>()
        taskListViewModel.updateTasksState.observeForever(observer)

        taskListViewModel.updateTasks(boardId, tasks)

        assertEquals(State<Unit>(error = errorMessage), taskListViewModel.updateTasksState.value)

        taskListViewModel.clearValues()

        assertEquals(null, taskListViewModel.updateTasksState.value)
    }
}