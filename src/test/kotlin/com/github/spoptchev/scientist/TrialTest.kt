package com.github.spoptchev.scientist

import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class TrialTest {

    private val trial = Trial("test", "test") {
        Thread.sleep(20)
        true
    }

    private val exceptionTrial = Trial("test", "test") {
        throw RuntimeException("e")
    }

    @Test
    fun `test observe`() {
        val observation = trial.run()

        assert(observation.duration >= Duration.ofMillis(20))
        assertEquals(observation.name, "test")
        assert(observation.startedAt < observation.stoppedAt)
    }

    @Test
    fun `test successful outcome`() {
        val observation = trial.run()

        assertEquals(Success(true), observation.outcome)
    }

    @Test
    fun `test failure outcome`() {
        val observation = exceptionTrial.run()

        assertTrue(observation.outcome.isFailure())
    }

}
