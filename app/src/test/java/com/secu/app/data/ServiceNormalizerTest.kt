package com.secu.app.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ServiceNormalizer")
class ServiceNormalizerTest {

    // ── normalize() ─────────────────────────────────────────────
    @Nested
    @DisplayName("normalize()")
    inner class Normalize {

        @Test
        @DisplayName("returns lowercase using Locale.ROOT")
        fun lowercasesInput() {
            assertEquals("github", ServiceNormalizer.normalize("GitHub"))
        }

        @Test
        @DisplayName("trims leading and trailing whitespace")
        fun trimsWhitespace() {
            assertEquals("slack", ServiceNormalizer.normalize("  Slack  "))
        }

        @Test
        @DisplayName("applies Unicode NFC normalization")
        fun appliesNfc() {
            // U+0065 (e) + U+0301 (combining acute) → U+00E9 (é) under NFC
            val decomposed = "e\u0301"
            val expected = "\u00E9" // precomposed é
            assertEquals(expected, ServiceNormalizer.normalize(decomposed))
        }

        @Test
        @DisplayName("combines lowercasing, trimming, and NFC in one pass")
        fun combinedPipeline() {
            // "  Café " with decomposed é → "café" with precomposed é
            val input = "  Cafe\u0301 "
            assertEquals("caf\u00E9", ServiceNormalizer.normalize(input))
        }

        @Test
        @DisplayName("throws IllegalArgumentException for blank input")
        fun rejectsBlank() {
            val ex = assertThrows(IllegalArgumentException::class.java) {
                ServiceNormalizer.normalize("   ")
            }
            assertEquals("Service name must not be blank", ex.message)
        }

        @Test
        @DisplayName("throws IllegalArgumentException for empty input")
        fun rejectsEmpty() {
            assertThrows(IllegalArgumentException::class.java) {
                ServiceNormalizer.normalize("")
            }
        }

        @Test
        @DisplayName("throws IllegalArgumentException when input contains a colon")
        fun rejectsColon() {
            val ex = assertThrows(IllegalArgumentException::class.java) {
                ServiceNormalizer.normalize("http:example")
            }
            assertEquals("Service name must not contain ':'", ex.message)
        }

        @Test
        @DisplayName("accepts input without forbidden characters")
        fun acceptsCleanInput() {
            assertEquals("my-service.com", ServiceNormalizer.normalize("My-Service.com"))
        }

        @Test
        @DisplayName("handles already-normalized input idempotently")
        fun idempotent() {
            val first = ServiceNormalizer.normalize("Dropbox")
            val second = ServiceNormalizer.normalize(first)
            assertEquals(first, second)
        }

        @Test
        @DisplayName("handles single character input")
        fun singleChar() {
            assertEquals("x", ServiceNormalizer.normalize("X"))
        }

        @Test
        @DisplayName("preserves digits and hyphens")
        fun digitsAndHyphens() {
            assertEquals("app-123", ServiceNormalizer.normalize("App-123"))
        }
    }

    // ── preview() ───────────────────────────────────────────────
    @Nested
    @DisplayName("preview()")
    inner class Preview {

        @Test
        @DisplayName("returns empty string when normalized value matches trimmed, lowercased original")
        fun identicalAfterNormalization() {
            // "slack" → normalized "slack", trimmedLower "slack" → no preview
            assertEquals("", ServiceNormalizer.preview("slack"))
        }

        @Test
        @DisplayName("returns empty string when only case differs (already handled by UI)")
        fun differsByCase() {
            // "GitHub" → normalized "github", trimmedLower "github" → no preview
            assertEquals("", ServiceNormalizer.preview("GitHub"))
        }

        @Test
        @DisplayName("returns empty string when only whitespace differs")
        fun differsByWhitespace() {
            // "  slack  " → normalized "slack", trimmedLower "slack" → no preview
            assertEquals("", ServiceNormalizer.preview("  slack  "))
        }

        @Test
        @DisplayName("returns normalized string for NFC changes")
        fun nfcPreview() {
            // decomposed "é" differs after normalization → preview the result
            val input = "e\u0301"
            val result = ServiceNormalizer.preview(input)
            assertEquals("\u00E9", result) // "é"
        }

        @Test
        @DisplayName("returns empty string for blank input (no preview)")
        fun blankInput() {
            assertEquals("", ServiceNormalizer.preview("   "))
        }

        @Test
        @DisplayName("returns empty string for colon input (no preview)")
        fun colonInput() {
            assertEquals("", ServiceNormalizer.preview("bad:name"))
        }

        @Test
        @DisplayName("returns empty string for empty input")
        fun emptyInput() {
            assertEquals("", ServiceNormalizer.preview(""))
        }
    }
}