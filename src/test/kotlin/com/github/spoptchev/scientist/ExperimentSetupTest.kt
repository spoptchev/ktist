package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals

class ExperimentSetupTest {

    private val setup = ExperimentSetup<Int, Unit>(
            name = "some-experiment",
            control = Trial(id = "test1", name = "control") { 1 },
            candidates = listOf(Trial(id = "test2", name = "candidate") { 2 }),
            conductible = { true }
    )

    @Test
    fun `test change control`() {
        val newSetup = setup.control("changed") { 1 }

        val oldExperiment = setup.complete()
        val newExperiment = newSetup.complete()

        assertEquals("control", oldExperiment.control.name)
        assertEquals("changed", newExperiment.control.name)
    }

    @Test
    fun `test add candidate`() {
        val newSetup = setup.candidate { 2 }

        val oldExperiment = setup.complete()
        val newExperiment = newSetup.complete()

        assertEquals(1, oldExperiment.candidates.size)
        assertEquals(2, newExperiment.candidates.size)
    }

    @Test
    fun `test change conductible`() {
        val context = {}
        val newSetup = setup.conductibleIf { false }

        val oldExperiment = setup.complete()
        val newExperiment = newSetup.complete()

        assertEquals(true, oldExperiment.conductible(context))
        assertEquals(false, newExperiment.conductible(context))
    }

    @Test
    fun `test experiment name`() {
        val experiment = setup.complete()

        assertEquals(experiment.name, "some-experiment")
    }

}
