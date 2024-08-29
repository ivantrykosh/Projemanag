package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.members

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.AssignMembersUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetCurrentUserUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user.GetUserByEmailUseCase
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
class MembersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var getUsersByIdsUseCase: GetUsersByIdsUseCase

    @Mock
    private lateinit var assignMembersUseCase: AssignMembersUseCase

    @Mock
    private lateinit var getUserByEmailUseCase: GetUserByEmailUseCase

    private lateinit var membersViewModel: MembersViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        membersViewModel = MembersViewModel(getUsersByIdsUseCase, assignMembersUseCase, getUserByEmailUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun getUsersByIds_emitSuccessState() = runTest {
        val usersIds = listOf("testUserId")
        val users = listOf(User("testUserId", "Test Name", "test@email.com"))
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Success(users)))

        val observer = mock<Observer<State<List<User>>>>()
        membersViewModel.getUsersByIdsState.observeForever(observer)

        membersViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(data = users))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUsersByIds_emitErrorState() = runTest {
        val usersIds = listOf("")
        val errorMessage = "Failed to get users by ids"
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<List<User>>>>()
        membersViewModel.getUsersByIdsState.observeForever(observer)

        membersViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUsersByIds_emitLoadingState() = runTest {
        val usersIds = listOf("testUserId")
        whenever(getUsersByIdsUseCase(usersIds)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<List<User>>>>()
        membersViewModel.getUsersByIdsState.observeForever(observer)

        membersViewModel.getUsersByIds(usersIds)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun assignMembers_emitSuccessState() = runTest {
        val boardId = "boardId"
        val members = listOf("testUserId")
        whenever(assignMembersUseCase(boardId, members)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        membersViewModel.assignMembersState.observeForever(observer)

        membersViewModel.assignMembers(boardId, members)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun assignMembers_emitErrorState() = runTest {
        val boardId = ""
        val members = listOf("testUserId")
        val errorMessage = "Failed to assign members"
        whenever(assignMembersUseCase(boardId, members)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        membersViewModel.assignMembersState.observeForever(observer)

        membersViewModel.assignMembers(boardId, members)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun assignMembers_emitLoadingState() = runTest {
        val boardId = "boardId"
        val members = listOf("testUserId")
        whenever(assignMembersUseCase(boardId, members)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        membersViewModel.assignMembersState.observeForever(observer)

        membersViewModel.assignMembers(boardId, members)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUserByEmail_emitSuccessState() = runTest {
        val email = "test@email.com"
        val user = User("testUserId", "Test Name", email)
        whenever(getUserByEmailUseCase(email)).thenReturn(flowOf(Resource.Success(user)))

        val observer = mock<Observer<State<User>>>()
        membersViewModel.getUserByEmailState.observeForever(observer)

        membersViewModel.getUserByEmail(email)

        verify(observer).onChanged(State(data = user))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUserByEmail_emitErrorState() = runTest {
        val email = "test@email.com"
        val errorMessage = "Failed to get user by email"
        whenever(getUserByEmailUseCase(email)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<User>>>()
        membersViewModel.getUserByEmailState.observeForever(observer)

        membersViewModel.getUserByEmail(email)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun getUserByEmail_emitLoadingState() = runTest {
        val email = "test@email.com"
        whenever(getUserByEmailUseCase(email)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<User>>>()
        membersViewModel.getUserByEmailState.observeForever(observer)

        membersViewModel.getUserByEmail(email)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}