package com.github.spoptchev.scientist

typealias ContextProvider<C> = () -> C

object NoContextProvider : ContextProvider<Unit> {
    override fun invoke() {}
}

class NotImplementedContextProvider<out C> : ContextProvider<C> {
    override fun invoke(): C = TODO("not implemented")
}
