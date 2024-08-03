package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.board

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.BoardRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssignMembersUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(id: String, members: List<String>) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            boardRepository.assignMembers(id, members)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}