package com.github.spoptchev.scientist


typealias Matcher<T> = (Outcome<T>, Outcome<T>) -> Boolean

class DefaultMatcher<in T> : Matcher<T> {
    override fun invoke(candidate: Outcome<T>, control: Outcome<T>): Boolean = when(candidate) {
        is Success -> when(control) {
            is Success -> candidate == control
            is Failure -> false
        }
        is Failure -> when(control) {
            is Success -> false
            is Failure -> candidate.errorMessage == control.errorMessage
        }
    }
}
