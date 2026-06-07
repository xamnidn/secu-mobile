package com.secu.app.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom

/**
 * Manages a cryptographically random, stable device identifier used for
 * device‑bound password derivation.
 *
 * The identifier is generated once (32 bytes → Base64 URL‑safe without padding)
 * and stored inside an [EncryptedSharedPreferences] instance secured by an
 * AES‑256‑GCM master key. Once created, the same value is returned for
 * subsequent calls until [clearDeviceId] is invoked.
 */
object DeviceIdManager {

    private const val PREFS_FILE = "secu_device_prefs"
    private const val KEY_DEVICE_ID = "secu_device_id"

    /**
     * Returns the current device ID, generating and persisting one if necessary.
     *
     * @param context Android context – uses [Context.applicationContext] internally.
     * @return A stable, URL‑safe Base64 device identifier (256‑bit entropy).
     */
    fun getDeviceId(context: Context): String {
        val prefs = getEncryptedPrefs(context.applicationContext)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (existing != null) {
            return existing
        }
        val newId = generateDeviceId()
        prefs.edit().putString(KEY_DEVICE_ID, newId).apply()
        return newId
    }

    /**
     * Removes the stored device identifier, forcing the next call to
     * [getDeviceId] to create a new value.
     */
    fun clearDeviceId(context: Context) {
        val prefs = getEncryptedPrefs(context.applicationContext)
        prefs.edit().remove(KEY_DEVICE_ID).apply()
    }

    /**
     * Builds an [EncryptedSharedPreferences] instance using the master key
     * from the Android Keystore (AES-256-GCM).
     */
    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_FILE,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Generates a 32‑byte random value and encodes it as URL‑safe Base64
     * without padding.
     */
    private fun generateDeviceId(): String {
        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(
            randomBytes,
            Base64.URL_SAFE or Base64.NO_PADDING
        )
    }
}