package com.github.spoptchev.scientist

import java.time.Clock
import java.time.Instant
import java.util.*

typealias Behaviour<T> = () -> T

data class Trial<T>(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        private val behaviour: Behaviour<T>
) {

    fun run(clock: Clock = NanoClock()): Observation<T> {
        val start = Instant.now(clock)
        val outcome = try { Success(behaviour()) } catch (e: Exception) { Failure<T>(e) }
        val stop = Instant.now(clock)

        return Observation(id, name, outcome, start, stop)
    }

}
