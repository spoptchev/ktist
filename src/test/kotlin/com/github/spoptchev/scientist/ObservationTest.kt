package com.github.spoptchev.scientist

import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import kotlin.test.assertEquals


class ObservationTest {

    private val fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
    private val value = 1
    private val outcome = Success(value)
    private val startedAt = Instant.now(fixedClock)

    private val baseObservation = Observation(
            id = "test",
            name = "test",
            outcome = outcome,
            startedAt = startedAt,
            stoppedAt = startedAt
    )

    @Test
    fun `test duration`() {
        val observation = baseObservation.copy(stoppedAt = startedAt.plusNanos(1))
        val expectedDuration = Duration.ofNanos(1)

        assertEquals(expectedDuration, observation.duration)
    }

    @Test
    fun `test successful result`() {
        val observation = baseObservation.copy(outcome = Success(2))

        assertEquals(2, observation.result)
    }

    @Test(expected = RuntimeException::class)
    fun `test result failure`() {
        val observation = baseObservation.copy(outcome = Failure(RuntimeException()))

        observation.result
    }

}
