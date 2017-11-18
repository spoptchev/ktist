@file:JvmName("TrialOutcome")
package com.github.spoptchev.scientist

sealed class Outcome<out T>
data class Success<out T>(val value: T) : Outcome<T>()
data class Failure<out T>(val throwable: Throwable) : Outcome<T>() {
    val errorMessage: String? by lazy { throwable.message }
}

fun <T> Outcome<T>.isSuccess(): Boolean = when(this) {
    is Success -> true
    is Failure -> false
}

fun <T> Outcome<T>.isFailure(): Boolean = !isSuccess()
