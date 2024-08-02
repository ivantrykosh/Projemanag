package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user_auth

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val userAuthRepository: UserAuthRepository
) {
    operator fun invoke(email: String, password: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.signIn(email, password)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}