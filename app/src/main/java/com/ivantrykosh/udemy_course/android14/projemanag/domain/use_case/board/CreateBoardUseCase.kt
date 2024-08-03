package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(board: Board) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            boardRepository.createBoard(board)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}