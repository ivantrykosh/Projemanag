package com.ivantrykosh.udemy_course.android14.projemanag.presenter

data class State<T>(
    val loading: Boolean = false,
    val error: String = "",
    val data: T? = null
)