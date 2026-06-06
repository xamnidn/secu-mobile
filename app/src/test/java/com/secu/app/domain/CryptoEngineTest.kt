package com.secu.app.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CryptoEngineTest {

    @Test
    fun `derivePassword produces deterministic output`() {
        val master = "mySecretMaster"
        val salt = "v1.3.0::login.example.com:1"
        val charset = "abcdefghijklmnopqrstuvwxyz0123456789"
        val length = 20

        val first = CryptoEngine.derivePassword(master, salt, charset, length)
        val second = CryptoEngine.derivePassword(master, salt, charset, length)
        assertEquals(first, second, "Same inputs must produce identical passwords")
    }

    @Test
    fun `derivePassword different master produces different password`() {
        val salt = "v1.3.0::test:1"
        val charset = "abc"
        val length = 10

        val p1 = CryptoEngine.derivePassword("master1", salt, charset, length)
        val p2 = CryptoEngine.derivePassword("master2", salt, charset, length)
        assertNotEquals(p1, p2, "Different master passwords must give different results")
    }

    @Test
    fun `derivePassword different service produces different password`() {
        val master = "commonMaster"
        val charset = "xyz"
        val length = 12

        val salt1 = "v1.3.0::serviceA:1"
        val salt2 = "v1.3.0::serviceB:1"
        val p1 = CryptoEngine.derivePassword(master, salt1, charset, length)
        val p2 = CryptoEngine.derivePassword(master, salt2, charset, length)
        assertNotEquals(p1, p2, "Different services (salts) must give different passwords")
    }

    @Test
    fun `derivePassword respects length`() {
        val length = 33
        val password = CryptoEngine.derivePassword("master", "salt", "abcdef", length)
        assertEquals(length, password.length, "Password length must match requested length")
    }

    @Test
    fun `derivePassword only uses characters from charset`() {
        val charset = "ABCDEF0123456789"
        val password = CryptoEngine.derivePassword("master", "salt", charset, 50)
        password.forEach { char ->
            assertTrue(char in charset, "Password contains character '$char' not in charset")
        }
    }

    @Test
    fun `derivePassword rejects empty charset`() {
        assertThrows<IllegalArgumentException> {
            CryptoEngine.derivePassword("master", "salt", "", 10)
        }
    }

    @Test
    fun `derivePassword matches desktop output`() {
        val masterPassword = "MyMasterPass"
        val salt = "v1.3.0::google.com:1"        // from SaltBuilder
        val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val length = 16

        // TODO: Replace with the actual expected output from the desktop reference implementation
        val expected = "EXPECTED_DESKTOP_OUTPUT"

        val actual = CryptoEngine.derivePassword(masterPassword, salt, charset, length)
        assertEquals(expected, actual, "Output must match desktop implementation")
    }
}