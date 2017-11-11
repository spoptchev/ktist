package com.github.spoptchev.ktist

import org.junit.Test
import kotlin.test.assertTrue


class ExperimentTest {

    private val control = Trial("control") { true }
    private val candidate = Trial("candidate") { false }
    private val experiment = Experiment("test", control, setOf(candidate))

    @Test
    fun `should return control value when conducting experiment`() {
        assertTrue(experiment.conduct())
    }

}
