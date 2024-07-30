package com.ivantrykosh.udemy_course.android14.projemanag.utils

sealed class Resource<T> (val data: T? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}