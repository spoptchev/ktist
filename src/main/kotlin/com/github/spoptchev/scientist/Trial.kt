package com.github.spoptchev.scientist

import java.time.Clock
import java.time.Instant
import java.util.*

typealias Behaviour<T> = () -> T

data class Trial<T>(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val catches: (Throwable) -> Boolean = { true },
        val behaviour: Behaviour<T>
): Comparable<Trial<T>> {

    fun run(clock: Clock = NanoClock()): Observation<T> {
        val start = Instant.now(clock)

        val outcome = try {
            Success(behaviour())
        } catch (e: Throwable) {
            if (catches(e)) Failure<T>(e) else throw e
        }

        val stop = Instant.now(clock)

        return Observation(id, name, outcome, start, stop)
    }

    fun refresh() = copy(id = UUID.randomUUID().toString())

    override fun compareTo(other: Trial<T>): Int = id.compareTo(other.id)

}
