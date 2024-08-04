package com.ivantrykosh.udemy_course.android14.projemanag.data.repository

import com.ivantrykosh.udemy_course.android14.projemanag.data.remote.firebase.FirebaseInstance
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.FirebaseInstanceRepository
import javax.inject.Inject

class FirebaseInstanceRepositoryImpl @Inject constructor(
    private val firebaseInstance: FirebaseInstance
): FirebaseInstanceRepository {

    override suspend fun getToken(): String {
        return firebaseInstance.getToken()
    }
}