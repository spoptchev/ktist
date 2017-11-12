package com.github.spoptchev.scientist

sealed class Outcome<out T>
data class Success<out T>(val value: T) : Outcome<T>()
data class Failure<out T>(val exception: Exception) : Outcome<T>() {
    val errorMessage: String? by lazy { exception.message }
}

fun <T> Outcome<T>.isSuccess(): Boolean = when(this) {
    is Success -> true
    is Failure -> false
}

fun <T> Outcome<T>.isFailure(): Boolean = !isSuccess()
