package com.github.spoptchev.scientist

typealias Publisher<T, C> = (Result<T, C>) -> Unit

class NullPublisher<T, in C> : Publisher<T, C> {
    override fun invoke(result: Result<T, C>) {}
}
