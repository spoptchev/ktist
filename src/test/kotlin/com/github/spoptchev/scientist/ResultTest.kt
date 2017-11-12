package com.github.spoptchev.scientist

import org.junit.Test
import java.time.Instant
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ResultTest {

    private val contextProvider = object : ContextProvider<Unit> {
        override fun invoke() = Unit
    }

    private val baseObservation = Observation(
            id = "base-id",
            name = "base-test",
            outcome = Success(true),
            startedAt = Instant.now(),
            stoppedAt = Instant.now()
    )

    private val baseResult = Result(
            experimentName = "test",
            observations = emptyList(),
            controlObservation = baseObservation.copy(id = "control-id", name = "control-test"),
            candidateObservations = listOf(baseObservation.copy(id = "candidate-id", name = "candidate-test")),
            mismatches = emptyList(),
            ignoredMismatches = emptyList(),
            contextProvider = contextProvider
    )

    @Test
    fun `test matched`() {
        val result = baseResult.copy()

        assertTrue(result.matched)
        assertFalse(result.ignored)
        assertFalse(result.mismatched)
    }

    @Test
    fun `test ignored`()  {
        val result = baseResult.copy(ignoredMismatches = listOf(baseObservation))

        assertFalse(result.matched)
        assertTrue(result.ignored)
        assertFalse(result.mismatched)
    }

    @Test
    fun `test mismatched`()  {
        val result = baseResult.copy(mismatches = listOf(baseObservation))

        assertFalse(result.matched)
        assertFalse(result.ignored)
        assertTrue(result.mismatched)
    }

}
