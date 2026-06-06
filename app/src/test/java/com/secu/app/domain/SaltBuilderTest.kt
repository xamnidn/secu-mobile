package com.secu.app.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Provides a compile-time constant for [BuildConfig.VERSION_NAME] in the unit test environment.
 * This avoids dependency on the Android-generated BuildConfig and keeps tests self-contained.
 */
object BuildConfig {
    const val VERSION_NAME = "1.3.0"
}

class SaltBuilderTest {

    @Test
    fun `build should prefix salt with version tag and colon`() {
        val salt = SaltBuilder.build("example.com")
        assertTrue(salt.startsWith("v${BuildConfig.VERSION_NAME}:"))
    }

    @Test
    fun `build should suffix salt with colon 1`() {
        val salt = SaltBuilder.build("example.com")
        assertTrue(salt.endsWith(":1"))
    }

    @Test
    fun `build without device component should produce empty device field`() {
        val salt = SaltBuilder.build("google.com")
        // The device component is empty, so two consecutive colons after version
        val expectedPrefix = "v${BuildConfig.VERSION_NAME}::"
        assertTrue(salt.startsWith(expectedPrefix))
    }

    @Test
    fun `build with device component should include it after version`() {
        val device = "abc123device-id"
        val salt = SaltBuilder.build("google.com", device)
        val expectedPrefix = "v${BuildConfig.VERSION_NAME}:$device:"
        assertTrue(salt.startsWith(expectedPrefix))
    }

    @Test
    fun `build should use normalized (lowercased) service name`() {
        val salt = SaltBuilder.build("Google.Com")
        // The service part should be lowercased after normalization
        assertTrue(salt.endsWith(":google.com:1"))
    }

    @Test
    fun `build should throw when service name is blank after normalization`() {
        val exception = assertThrows<IllegalArgumentException> {
            SaltBuilder.build("   ")
        }
        assertEquals("Service name must not be blank", exception.message)
    }

    @Test
    fun `build should throw when service name contains a colon`() {
        val exception = assertThrows<IllegalArgumentException> {
            SaltBuilder.build("evil:com")
        }
        // Exception originates from ServiceNormalizer, message should be about colon
        assertTrue(exception.message?.contains("colon") == true)
    }

    @Test
    fun `build salt must not contain any whitespace`() {
        val salt = SaltBuilder.build("example.com", "dev123")
        assertFalse(salt.contains(" "))
        assertFalse(salt.contains("\t"))
    }

    @Test
    fun `build should produce exactly four colon-separated parts`() {
        val salt = SaltBuilder.build("service", "device")
        val parts = salt.split(":")
        assertEquals(4, parts.size)
        assertEquals("1", parts.last())
    }

    @Test
    fun `build should handle empty device component correctly in full pattern`() {
        val salt = SaltBuilder.build("my.service")
        // format: v1.3.0::my.service:1
        val regex = Regex("v\\d+\\.\\d+\\.\\d+::my\\.service:1")
        assertTrue(salt.matches(regex))
    }
}