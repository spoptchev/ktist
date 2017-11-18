package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        assertEquals(false, experiment.conductible(context))
    }

    @Test
    fun `test experiment name`() {
        val experiment = setup
                .experiment { "some-experiment" }
                .complete()

        assertEquals("some-experiment", experiment.name)
    }

    @Test
    fun `test catch`() {
        val experiment = setup
                .control { 1 }
                .candidate { 1 }
                .catch { e: Throwable -> e is NumberFormatException }
                .complete()

        val e1 = NumberFormatException("")
        val e2 = NullPointerException("")

        assertTrue(experiment.control.catches(e1))
        assertTrue(experiment.candidates.all { it.catches(e1) })
        assertFalse(experiment.control.catches(e2))
        assertFalse(experiment.candidates.all { it.catches(e2) })
    }

}
