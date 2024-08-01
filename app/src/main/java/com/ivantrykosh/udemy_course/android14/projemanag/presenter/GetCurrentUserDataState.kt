package com.ivantrykosh.udemy_course.android14.projemanag.presenter

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User

data class GetCurrentUserDataState(
    val loading: Boolean = false,
    val error: String = "",
    val data: User? = null
)
