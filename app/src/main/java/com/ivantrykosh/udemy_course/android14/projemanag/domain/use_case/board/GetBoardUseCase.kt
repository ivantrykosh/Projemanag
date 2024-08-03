package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(id: String) = flow {
        try {
            emit(Resource.Loading())
            val board = boardRepository.getBoard(id)
            emit(Resource.Success(board))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}