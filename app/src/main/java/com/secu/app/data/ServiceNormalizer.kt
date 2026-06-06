package com.secu.app.data

import java.text.Normalizer
import java.util.Locale

/**
 * Normalizes service or website names into a canonical form
 * before they are used as input for password generation.
 *
 * The canonical pipeline:
 *   1. Validate: reject blank input and input containing ':'
 *   2. Unicode NFC normalization (compose precomposed characters)
 *   3. Lowercase using [Locale.ROOT] (locale-independent)
 *   4. Trim leading/trailing whitespace
 *
 * Colons are rejected because they serve as field delimiters
 * in the SECU salt format.
 */
object ServiceNormalizer {

    private const val FORBIDDEN_CHAR = ':'

    /**
     * Normalizes [raw] into its canonical form.
     *
     * @param raw The user-supplied service or website name.
     * @return The normalized string, ready for password derivation.
     * @throws IllegalArgumentException if [raw] is blank or contains a colon.
     */
    fun normalize(raw: String): String {
        require(raw.isNotBlank()) { "Service name must not be blank" }
        require(FORBIDDEN_CHAR !in raw) {
            "Service name must not contain '$FORBIDDEN_CHAR'"
        }
        return applyNormalization(raw)
    }

    /**
     * Returns a preview of the normalized form, suitable for display
     * in the UI. The UI prefixes this with "Preview: " when the result
     * is non-empty and differs from the trimmed original.
     *
     * - If normalization succeeds and the result differs from the
     *   trimmed, lowercased original, returns the normalized value.
     * - If the result is identical to the trimmed, lowercased original,
     *   returns an empty string (no preview needed).
     * - If normalization fails (blank input, colon), returns an
     *   empty string instead of throwing.
     *
     * @param raw The user-supplied service or website name.
     * @return The normalized string if it visibly changes the input,
     *         otherwise an empty string.
     */
    fun preview(raw: String): String {
        return try {
            val normalized = normalize(raw)
            val trimmedLower = raw.trim().lowercase(Locale.ROOT)
            if (normalized == trimmedLower) "" else normalized
        } catch (_: IllegalArgumentException) {
            ""
        }
    }

    /**
     * Applies the core normalization steps: NFC, lowercase, trim.
     */
    private fun applyNormalization(raw: String): String {
        return Normalizer.normalize(raw, Normalizer.Form.NFC)
            .lowercase(Locale.ROOT)
            .trim()
    }
}