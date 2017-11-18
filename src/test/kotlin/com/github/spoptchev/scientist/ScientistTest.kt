package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.fail


class ScientistTest {

    class CatchingPublisher(var caughtResult: Result<Boolean, Unit>?) : Publisher<Boolean, Unit> {
        override fun invoke(result: Result<Boolean, Unit>) {
            caughtResult = result
        }
    }

    private val contextProvider = NoContextProvider
    private val exception = RuntimeException("Test")
    private val controlTrial = Trial(name = "control-trial") { true }
    private val candidateTrial = Trial(name = "candidate-trial") { false }
    private val exceptionTrial = Trial<Boolean>(name = "candidate-exception") { throw exception }

    private val baseExperiment = DefaultExperiment<Boolean, Unit>(
            name = "test",
            control = controlTrial,
            candidates = listOf(candidateTrial, exceptionTrial)
    )

    private val ignore = { candidate: Outcome<Boolean>, _: Outcome<Boolean> -> candidate.isFailure() }

    private val scientist = Scientist(
            contextProvider = contextProvider,
            ignores = listOf(ignore)
    )

    @Test
    fun `test evaluate experiment`() {
        val publisher = CatchingPublisher(null)

        val result = scientist
                .copy(publish = publisher)
                .evaluate(baseExperiment)

        val publishedResult = publisher.caughtResult!!

        assertTrue(result)
        assertEquals(3, publishedResult.observations.size)
        assertEquals(2, publishedResult.candidateObservations.size)
        assertEquals(1, publishedResult.mismatches.size)
        assertEquals(1, publishedResult.ignoredMismatches.size)
        assertEquals(Success(true), publishedResult.controlObservation.outcome)
        assertTrue(publishedResult.candidateObservations.map { it.outcome }.contains(Success(false)))
        assertEquals(Failure(exception), publishedResult.ignoredMismatches.first().outcome)
        assertEquals(contextProvider, publishedResult.contextProvider)
        assertTrue(publishedResult.mismatched)
        assertTrue(publishedResult.ignored)
        assertFalse(publishedResult.matched)
    }

    @Test(expected = MismatchException::class)
    fun `test throw on mismatches`() {
        scientist
                .copy(throwOnMismatches = true)
                .evaluate(baseExperiment)

        fail("Expected to fail with MismatchException")
    }

}
