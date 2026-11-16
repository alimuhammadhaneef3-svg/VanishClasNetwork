package com.example.vanishclassnetwork

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun validateDisplayName_empty_fails() {
        val result = validateDisplayName("")
        assertTrue(result.isFailure)
        assertEquals("Display name cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun validateDisplayName_tooShort_fails() {
        val result = validateDisplayName("Ab")
        assertTrue(result.isFailure)
        assertEquals("Name must be at least 3 characters", result.exceptionOrNull()?.message)
    }

    @Test
    fun validateDisplayName_valid_succeeds() {
        val result = validateDisplayName("Alice")
        assertTrue(result.isSuccess)
        assertEquals("Alice", result.getOrNull())
    }
}
