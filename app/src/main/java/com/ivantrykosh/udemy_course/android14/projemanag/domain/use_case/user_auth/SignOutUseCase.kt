package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke() = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.signOut()
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}