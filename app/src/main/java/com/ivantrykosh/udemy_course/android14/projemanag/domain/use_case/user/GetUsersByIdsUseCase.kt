package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetUsersByIdsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userIds: List<String>) = flow {
        try {
            emit(Resource.Loading())
            val users = userRepository.getUsersByIds(userIds)
            emit(Resource.Success(users))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}