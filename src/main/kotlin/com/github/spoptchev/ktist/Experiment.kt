package com.github.spoptchev.ktist

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

sealed class Outcome<out T>
data class Success<out T>(val value: T) : Outcome<T>()
data class Failure<out T>(val exception: Exception) : Outcome<T>()

typealias Behaviour<T> = () -> T

data class Observation<out T>(
        val name: String,
        private val outcome: Outcome<T>,
        val startedAt: Instant,
        val stoppedAt: Instant
) {

    val duration: Duration by lazy {
        Duration.between(startedAt, stoppedAt)
    }

    val result: T by lazy {
        when(outcome) {
            is Success<T> -> outcome.value
            is Failure<T> -> throw outcome.exception
        }
    }

}

data class Trial<out T>(val name: String, private val behaviour: Behaviour<T>) {

    fun run(clock: Clock = NanoClock()): Observation<T> {
        val start = Instant.now(clock)
        val outcome = try { Success(behaviour()) } catch (e: Exception) { Failure<T>(e) }
        val stop = Instant.now(clock)

        return Observation(name, outcome, start, stop)
    }

}

data class Experiment<out T>(
        val name: String,
        private val control: Trial<T>,
        private val candidates: Set<Trial<T>>
) {

    private val shuffledTrials: List<Trial<T>> by lazy {
        (listOf(control) + candidates.toList()).shuffle()
    }

    fun conduct(): T {
        val observationByTrail = shuffledTrials
                .map { Pair(it, it.run()) }
                .toMap()

        return observationByTrail[control]!!.result
    }

}

private fun <T> Iterable<T>.shuffle(): List<T> = toMutableList()
        .apply { Collections.shuffle(this) }
        .toList()

private class NanoClock(val clock: Clock = Clock.systemUTC()) : Clock() {

    private val initialInstant = Instant.now(clock)
    private val initialNanos = System.nanoTime()

    override fun withZone(zone: ZoneId): Clock = NanoClock(clock.withZone(zone))

    override fun getZone(): ZoneId = clock.zone

    override fun instant(): Instant = initialInstant.plusNanos(System.nanoTime() - initialNanos)

}
