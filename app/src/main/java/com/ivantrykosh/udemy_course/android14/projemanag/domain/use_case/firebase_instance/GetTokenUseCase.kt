package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_instance

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseInstanceRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val firebaseInstanceRepository: FirebaseInstanceRepository
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val token = firebaseInstanceRepository.getToken()
            emit(Resource.Success(token))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}