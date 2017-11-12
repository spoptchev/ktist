package com.github.spoptchev.scientist

sealed class Outcome<out T>
data class Success<out T>(val value: T) : Outcome<T>()
data class Failure<out T>(val exception: Exception) : Outcome<T>()
