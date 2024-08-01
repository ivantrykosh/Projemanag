package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val user = userRepository.getCurrentUserData()
            emit(Resource.Success(user))
        } catch (e: Exception) { // todo make exception more detailed
            emit(Resource.Error("ERROR"))
        }
    }
}