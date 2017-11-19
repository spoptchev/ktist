package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals


class ContextProviderTest {

    @Test
    fun `test no context provider`() {
        val contextProvider = NoContextProvider
        val result = contextProvider()

        assertEquals(Unit, result)
    }

    @Test(expected = NotImplementedError::class)
    fun `test not implemented context provider`() {
        val contextProvider = NotImplementedContextProvider<Boolean>()

        contextProvider()
    }

}
