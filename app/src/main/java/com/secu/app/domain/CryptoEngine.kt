package com.secu.app.domain

import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object CryptoEngine {

    private const val ITERATIONS = 3
    private const val MEMORY = 65536
    private const val PARALLELISM = 4
    private const val HASH_LENGTH = 32

    fun buildCharset(upper: Boolean, lower: Boolean, numbers: Boolean, symbols: Boolean): String {
        val sb = StringBuilder()
        if (upper) sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        if (lower) sb.append("abcdefghijklmnopqrstuvwxyz")
        if (numbers) sb.append("0123456789")
        if (symbols) sb.append("!@#\$%^&*()_+-=[]{}|;:,.<>?/~")
        require(sb.isNotEmpty()) { "At least one character set must be selected" }
        return sb.toString()
    }

    fun derivePassword(masterPassword: String, salt: String, charset: String, length: Int): String {
        require(charset.isNotEmpty()) { "Charset must not be empty" }

        val passwordBytes = masterPassword.toByteArray(StandardCharsets.UTF_8)
        val saltBytes = salt.toByteArray(StandardCharsets.UTF_8)
        val hash = ByteArray(HASH_LENGTH)

        try {
            val params = Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM)
                .withSalt(saltBytes)
                .build()

            val generator = Argon2BytesGenerator()
            generator.init(params)
            generator.generateBytes(passwordBytes, hash)

            val password = expandPassword(hash, charset, length)
            hash.fill(0)
            return password
        } finally {
            passwordBytes.fill(0)
            saltBytes.fill(0)
        }
    }

    private fun expandPassword(hashBytes: ByteArray, charset: String, length: Int): String {
        val charsetChars = charset.toCharArray()
        val threshold = 256 - (256 % charset.length)
        val password = StringBuilder(length)
        var counter = 0

        while (password.length < length) {
            val counterBytes = ByteBuffer.allocate(4).putInt(counter).array()
            val digest = MessageDigest.getInstance("SHA-256").digest(hashBytes + counterBytes)

            for (byte in digest) {
                val unsigned = byte.toInt() and 0xFF
                if (unsigned < threshold) {
                    password.append(charsetChars[unsigned % charset.length])
                    if (password.length == length) break
                }
            }
            counter++
        }
        return password.toString()
    }
}