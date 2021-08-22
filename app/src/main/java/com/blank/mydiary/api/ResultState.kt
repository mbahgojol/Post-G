package com.blank.mydiary.api

sealed class ResultState {
    class Success<T>(val data: T) : ResultState()
    class Error(val e: Throwable) : ResultState()
    class Loading(val loading: Boolean) : ResultState()
}
