package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.firebase_messaging

import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseMessagingRepository
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Resource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(
    private val firebaseMessagingRepository: FirebaseMessagingRepository
) {
    operator fun invoke() = flow {
        try {
            emit(Resource.Loading())
            val token = firebaseMessagingRepository.getToken()
            emit(Resource.Success(token))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}