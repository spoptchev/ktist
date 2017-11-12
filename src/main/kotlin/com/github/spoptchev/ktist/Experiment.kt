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
typealias Comparator<T> = (Outcome<T>, Outcome<T>) -> Boolean
typealias Ignore<T> = (Outcome<T>, Outcome<T>) -> Boolean

sealed class Trial<T> {
    abstract val id: String
    abstract val name: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Trial<*>

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

data class OpenTrial<T>(
        override val id: String = UUID.randomUUID().toString(),
        override val name: String,
        private val behaviour: Behaviour<T>
) : Trial<T>() {

    fun run(clock: Clock = NanoClock()): Observation<T> {
        val start = Instant.now(clock)
        val outcome = try { Success(behaviour()) } catch (e: Exception) { Failure<T>(e) }
        val stop = Instant.now(clock)

        return Observation(id, name, outcome, start, stop)
    }

}

data class Observation<T>(
        override val id: String,
        override val name: String,
        val outcome: Outcome<T>,
        val startedAt: Instant,
        val stoppedAt: Instant
) : Trial<T>() {

    val duration: Duration by lazy {
        Duration.between(startedAt, stoppedAt)
    }

    val result: T by lazy {
        when(outcome) {
            is Success<T> -> outcome.value
            is Failure<T> -> throw outcome.exception
        }
    }

    fun matches(other: Observation<T>, comparator: Comparator<T>): Boolean {
        return comparator(outcome, other.outcome)
    }

    fun isIgnored(other: Observation<T>, ignores: List<Ignore<T>>): Boolean {
        return ignores.any { it(outcome, other.outcome) }
    }

}

sealed class Experiment<T> {
    abstract val name: String
}

data class OpenExperiment<T>(
        override val name: String,
        private val control: OpenTrial<T>,
        private val candidates: List<OpenTrial<T>>
) : Experiment<T>() {

    private val shuffledTrials: List<OpenTrial<T>> by lazy {
        (listOf(control) + candidates).sortedBy { it.id }
    }

    fun conduct(): ConductedExperiment<T> {
        val observations = shuffledTrials.map { it.run() }
        val controlObservation = observations.first { it == control }
        val candidateObservations = observations - controlObservation

        return ConductedExperiment(name, observations, controlObservation, candidateObservations)
    }

}

data class ConductedExperiment<T>(
        override val name: String,
        val observations: List<Observation<T>>,
        val controlObservation: Observation<T>,
        val candidateObservations: List<Observation<T>>
) : Experiment<T>()


private class NanoClock(val clock: Clock = Clock.systemUTC()) : Clock() {

    private val initialInstant = Instant.now(clock)
    private val initialNanos = System.nanoTime()

    override fun withZone(zone: ZoneId): Clock = NanoClock(clock.withZone(zone))

    override fun getZone(): ZoneId = clock.zone

    override fun instant(): Instant = initialInstant.plusNanos(System.nanoTime() - initialNanos)

}
