package com.github.spoptchev.scientist

import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail


class TrialTest {

    private val trial = Trial("test", "test") {
        Thread.sleep(20)
        true
    }

    private val exceptionTrial = Trial("test", "test") {
        throw NumberFormatException("e")
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

    @Test(expected = NumberFormatException::class)
    fun `test throw exception when not caught`() {
        val trial = exceptionTrial.copy(catches = { e -> e is NullPointerException })

        trial.run()

        fail("expected to throw NumberFormatException")
    }

    @Test
    fun `test refresh to generate a new id`() {
        val currentId = trial.id
        val newId = trial.refresh().id

        assertNotEquals(currentId, newId)
    }

    @Test
    fun `test compareTo`() {
        val trial1 = Trial(id = "Z", name = "test") { }
        val trial2 = Trial(id = "A", name = "test") { }
        val trial3 = Trial(id = "A", name = "test") { }

        assertTrue(trial1 > trial2)
        assertTrue(trial2 >= trial3)
    }

}
