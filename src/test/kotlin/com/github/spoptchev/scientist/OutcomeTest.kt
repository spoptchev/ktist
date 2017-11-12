package com.github.spoptchev.scientist

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class OutcomeTest {

    private val success = Success(true)
    private val failure = Failure<Boolean>(RuntimeException("test"))

    @Test
    fun `test success`() {
        assertTrue(success.isSuccess())
        assertFalse(success.isFailure())
    }

    @Test
    fun `test failure`() {
        assertFalse(failure.isSuccess())
        assertTrue(failure.isFailure())
    }
}
