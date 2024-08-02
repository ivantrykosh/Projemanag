package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserAuthRepository
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
) {
    operator fun invoke(email: String, password: String, name: String) = flow<Resource<Unit>> {
        try {
            emit(Resource.Loading())
            userAuthRepository.createUser(email, password)
            val user = User(email = email, name = name)
            userRepository.createUser(user)
            emit(Resource.Success())
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}