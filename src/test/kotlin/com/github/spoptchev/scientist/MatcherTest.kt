package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MatcherTest {

    val success1 = Success(true)
    val success2 = Success(false)
    val failure1 = Failure<Boolean>(RuntimeException("failure1"))
    val failure2 = Failure<Boolean>(RuntimeException("failure2"))
    val match = DefaultMatcher<Boolean>()

    @Test
    fun `test when both success and same value`() {
        val result = match(success1, Success(true))

        assertTrue(result)
    }

    @Test
    fun `test when both success but different value`() {
        val result = match(success1, success2)

        assertFalse(result)
    }

    @Test
    fun `test when one success and other failure`() {
        val result = match(success1, failure1)

        assertFalse(result)
    }

    @Test
    fun `test when both failure with same error message`() {
        val result = match(failure1, Failure(RuntimeException("failure1")))

        assertTrue(result)
    }

    @Test
    fun `test when both failure with different error message`() {
        val result = match(failure1, failure2)

        assertFalse(result)
    }

}
