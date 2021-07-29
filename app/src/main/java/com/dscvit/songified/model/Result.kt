package com.dscvit.songified.model

import androidx.annotation.Keep

@Keep
sealed class Result<out T> {

    data class Success<out T>(val data: T? = null) : Result<T>()

    data class Error<out T>(val message: String? = null) : Result<T>()

    class Loading<out T> : Result<T>()
}
