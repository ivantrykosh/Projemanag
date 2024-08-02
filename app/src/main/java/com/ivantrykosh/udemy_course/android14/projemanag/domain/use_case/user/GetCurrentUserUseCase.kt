package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val user = userRepository.getCurrentUser()
            emit(Resource.Success(user))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}