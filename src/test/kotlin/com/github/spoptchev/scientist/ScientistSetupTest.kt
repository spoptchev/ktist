package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals


class ScientistSetupTest {

    private val setup = ScientistSetup<Int, Unit>()

    @Test
    fun `test change publisher`() {
        val publisher = { _ : Result<Int, Unit> -> }
        val scientist = setup
                .publisher(publisher)
                .complete()

        assertEquals(publisher, scientist.publish)
    }

    @Test
    fun `test add ignore`() {
        val ignore = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val scientist = setup
                .ignore(ignore)
                .ignore({ _: Outcome<Int>, _: Outcome<Int> -> true })
                .complete()

        assertEquals(ignore, scientist.ignores.first())
        assertEquals(2, scientist.ignores.size)
    }

    @Test
    fun `test change matcher`() {
        val matcher = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val scientist = setup
                .match(matcher)
                .complete()

        assertEquals(matcher, scientist.matcher)
    }

    @Test
    fun `test context provider`() {
        val scientist = setup
                .context(NoContextProvider)
                .complete()

        assertEquals(NoContextProvider, scientist.contextProvider)
    }

}
