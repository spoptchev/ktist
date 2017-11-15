package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals


class ScientistDSLTest {

    @Test
    fun `test scientist dsl`() {
        val result = scientist<Int, Unit> {
            context {}
        } conduct {
            experiment { "experiment-dsl" }
            control("control") { 1 }
            candidate("candidate") { 1 }
        }

        assertEquals(1, result)
    }

}
