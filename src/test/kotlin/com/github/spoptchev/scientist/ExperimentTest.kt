package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertTrue


class ExperimentTest {

    private val controlTrail = Trial(name = "control-trail") { true }
    private val candidateTrail = Trial(name = "candidate-trail") { false }
    private val contextProvider = object : ContextProvider<Unit> {
        override fun invoke() = Unit
    }

    private val baseExperiment = DefaultExperiment<Boolean, Unit>(
            name = "test",
            control = controlTrail,
            candidates = listOf(candidateTrail)
    )

    @Test
    fun `should return a skipped experiment state when not conductible`() {
        val experiment = baseExperiment.copy(conductible = { false })
        val state = experiment.conduct(contextProvider)

        assertTrue(state is Skipped)
    }

    @Test
    fun `should return a conducted experiment when its conductible`() {
        val experiment = baseExperiment.copy(conductible = { true })
        val state = experiment.conduct(contextProvider)

        assertTrue(state is Conducted)
    }

}
