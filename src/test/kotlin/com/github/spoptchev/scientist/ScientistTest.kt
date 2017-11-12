package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ScientistTest {

    class CatchingPublisher(var caughtResult: Result<Boolean, Unit>?) : Publisher<Boolean, Unit> {
        override fun invoke(result: Result<Boolean, Unit>) {
            caughtResult = result
        }
    }

    private val publisher = CatchingPublisher(null)
    private val contextProvider = object : ContextProvider<Unit> {
        override fun invoke() = Unit
    }
    private val exception = RuntimeException("Test")
    private val controlTrial = Trial(name = "control-trial") { true }
    private val candidateTrial = Trial(name = "candidate-trial") { false }
    private val exceptionTrial = Trial<Boolean>(name = "candidate-exception") { throw exception }

    private val baseExperiment = DefaultExperiment<Boolean, Unit>(
            name = "test",
            control = controlTrial,
            candidates = listOf(candidateTrial, exceptionTrial)
    )

    private val ignore: Ignore<Boolean> = { candidate, _ -> candidate is Failure }

    private val scientist = Scientist(
            contextProvider = contextProvider,
            publish = publisher,
            ignores = listOf(ignore)
    )

    @Test
    fun `test evaluate experiment`() {
        val result = scientist.evaluate(baseExperiment)
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

}
