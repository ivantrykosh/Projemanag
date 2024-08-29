package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.create_board

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board.CreateBoardUseCase
import com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_storage.UploadImageUseCase
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
class CreateBoardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Mock
    private lateinit var getCurrentUserIdUseCase: GetCurrentUserIdUseCase

    @Mock
    private lateinit var uploadImageUseCase: UploadImageUseCase

    @Mock
    private lateinit var createBoardUseCase: CreateBoardUseCase

    private lateinit var createBoardViewModel: CreateBoardViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        createBoardViewModel = CreateBoardViewModel(uploadImageUseCase, createBoardUseCase, getCurrentUserUseCase, getCurrentUserIdUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }

    @Test
    fun uploadImage_emitSuccessState() = runTest {
        val newName = "image123.jpg"
        val imageUri = mock<Uri>()
        val downloadUrl = "downloadUrl"
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Success(downloadUrl)))

        val observer = mock<Observer<State<String>>>()
        createBoardViewModel.uploadImageState.observeForever(observer)

        createBoardViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(data = downloadUrl))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun uploadImage_emitErrorState() = runTest {
        val newName = "image123.jpg"
        val imageUri = mock<Uri>()
        val errorMessage = "Failed to upload image"
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<String>>>()
        createBoardViewModel.uploadImageState.observeForever(observer)

        createBoardViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun uploadImage_emitLoadingState() = runTest {
        val newName = "image123.jpg"
        val imageUri = mock<Uri>()
        whenever(uploadImageUseCase(newName, imageUri)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<String>>>()
        createBoardViewModel.uploadImageState.observeForever(observer)

        createBoardViewModel.uploadImage(newName, imageUri)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun createBoard_emitSuccessState() = runTest {
        val name = "Test Board"
        val imageUrl = "boardImageUrl"
        val createdBy = "testUserId"
        val assignedTo = arrayListOf(createdBy)
        val newBoard = Board(
            name = name,
            image = imageUrl,
            createdBy = createdBy,
            assignedTo = assignedTo,
        )
        whenever(createBoardUseCase(newBoard)).thenReturn(flowOf(Resource.Success()))

        val observer = mock<Observer<State<Unit>>>()
        createBoardViewModel.createBoardState.observeForever(observer)

        createBoardViewModel.createBoard(name, imageUrl, createdBy, assignedTo)

        verify(observer).onChanged(State())
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun createBoard_emitErrorState() = runTest {
        val name = ""
        val imageUrl = "boardImageUrl"
        val createdBy = ""
        val assignedTo = arrayListOf(createdBy)
        val newBoard = Board(
            name = name,
            image = imageUrl,
            createdBy = createdBy,
            assignedTo = assignedTo,
        )
        val errorMessage = "Failed to create board"
        whenever(createBoardUseCase(newBoard)).thenReturn(flowOf(Resource.Error(errorMessage)))

        val observer = mock<Observer<State<Unit>>>()
        createBoardViewModel.createBoardState.observeForever(observer)

        createBoardViewModel.createBoard(name, imageUrl, createdBy, assignedTo)

        verify(observer).onChanged(State(error = errorMessage))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun createBoard_emitLoadingState() = runTest {
        val name = "Test Board"
        val imageUrl = "boardImageUrl"
        val createdBy = "testUserId"
        val assignedTo = arrayListOf(createdBy)
        val newBoard = Board(
            name = name,
            image = imageUrl,
            createdBy = createdBy,
            assignedTo = assignedTo,
        )
        whenever(createBoardUseCase(newBoard)).thenReturn(flowOf(Resource.Loading()))

        val observer = mock<Observer<State<Unit>>>()
        createBoardViewModel.createBoardState.observeForever(observer)

        createBoardViewModel.createBoard(name, imageUrl, createdBy, assignedTo)

        verify(observer).onChanged(State(loading = true))
        verifyNoMoreInteractions(observer)
    }
}