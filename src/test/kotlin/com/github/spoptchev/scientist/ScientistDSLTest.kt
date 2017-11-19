package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals


class ScientistDSLTest {

    @Test
    fun `test scientist dsl with scientist and experiment setup`() {
        val result = scientist<Int, Unit> {
            context {}
        } conduct {
            experiment { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        assertEquals(1, result)
    }

    @Test
    fun `test default scientist`() {
        val result = scientist<Int, Unit>() conduct {
            experiment { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        assertEquals(1, result)

    }

    @Test
    fun `test with experiment`() {
        val result = scientist<Int, Unit>() conduct experiment {
            name { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        assertEquals(1, result)
    }

}
