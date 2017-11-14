package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class ScientistSetupTest {

    private val setup = ScientistSetup<Int, Unit>(
            contextProvider = NoContextProvider,
            publish = NullPublisher(),
            ignores = emptyList(),
            matcher = DefaultMatcher()
    )

    @Test
    fun `test change publisher`() {
        val publisher = { _ : Result<Int, Unit> -> }
        val newSetup = setup.publisher(publisher)

        val oldScientist = setup.complete()
        val newScientist = newSetup.complete()

        assertNotEquals(publisher, oldScientist.publish)
        assertEquals(publisher, newScientist.publish)
    }

    @Test
    fun `test add ignore`() {
        val ignore = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val newSetup = setup.ignore(ignore)

        val oldScientist = setup.complete()
        val newScientist = newSetup.complete()

        assertNotEquals(ignore, oldScientist.ignores.firstOrNull())
        assertEquals(ignore, newScientist.ignores.first())
    }

    @Test
    fun `test change matcher`() {
        val matcher = { _: Outcome<Int>, _: Outcome<Int> -> false }
        val newSetup = setup.match(matcher)

        val oldScientist = setup.complete()
        val newScientist = newSetup.complete()

        assertNotEquals(matcher, oldScientist.matcher)
        assertEquals(matcher, newScientist.matcher)
    }

    @Test
    fun `test context provider`() {
        val scientist = setup.complete()

        assertEquals(NoContextProvider, scientist.contextProvider)
    }

}
