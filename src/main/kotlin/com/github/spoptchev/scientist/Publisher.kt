package com.github.spoptchev.scientist

interface Publisher<T, in C> : (Result<T, C>) -> Unit

class NullPublisher<T, in C> : Publisher<T, C> {
    override fun invoke(result: Result<T, C>) {}
}
