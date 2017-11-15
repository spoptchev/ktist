package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals

class ExperimentSetupTest {

    private val setup = ExperimentSetup<Int, Unit>()
            .control("default-control") { 2 }

    @Test
    fun `test change control`() {
        val experiment = setup
                .control("changed") { 1 }
                .complete()

        assertEquals("changed", experiment.control.name)
    }

    @Test
    fun `test add candidate`() {
        val experiment = setup
                .candidate { 2 }
                .candidate { 3 }
                .complete()

        assertEquals(2, experiment.candidates.size)
    }

    @Test
    fun `test change conductible`() {
        val context = NoContextProvider
        val experiment = setup
                .conductibleIf { false }
                .complete()

        assertEquals(false, experiment.conductible(context()))
    }

    @Test
    fun `test experiment name`() {
        val experiment = setup
                .experiment { "some-experiment" }
                .complete()

        assertEquals(experiment.name, "some-experiment")
    }

}
