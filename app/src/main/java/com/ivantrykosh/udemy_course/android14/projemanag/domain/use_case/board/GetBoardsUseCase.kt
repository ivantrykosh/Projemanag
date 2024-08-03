package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBoardsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val boards = boardRepository.getBoards()
            emit(Resource.Success(boards))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}