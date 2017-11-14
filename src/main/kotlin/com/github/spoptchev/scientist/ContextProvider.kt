package com.github.spoptchev.scientist

typealias ContextProvider<C> = () -> C

object NoContextProvider : ContextProvider<Unit> {
    override fun invoke() {}
}
