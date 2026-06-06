package com.secu.app.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.security.SecureRandom

/**
 * Singleton that manages clipboard operations with automatic clearing
 * and noise overwrite after 10 seconds.
 *
 * The overwrite replaces the clipboard content with 48 bytes of random hex
 * to minimise the risk of password leakage through clipboard history or
 * third-party clipboard managers.
 */
object ClipboardManager {

    private const val LABEL_PASSWORD = "SECU Password"
    private const val LABEL_CLEARED = "SECU Cleared"
    private const val CLEAR_DELAY_MS = 10_000L
    private const val NOISE_BYTES = 48

    private val handler = Handler(Looper.getMainLooper())
    private var clearRunnable: Runnable? = null

    /**
     * Copies [text] to the system clipboard and schedules an automatic
     * overwrite after [CLEAR_DELAY_MS].
     *
     * If a previous clear is still pending, its timer is reset.
     */
    fun copy(context: Context, text: String) {
        val clipboard =
            context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(LABEL_PASSWORD, text))

        // Reset any pending clear timer
        clearRunnable?.let { handler.removeCallbacks(it) }
        val runnable = Runnable { overwrite(context.applicationContext) }
        clearRunnable = runnable
        handler.postDelayed(runnable, CLEAR_DELAY_MS)
    }

    /**
     * Immediately overwrites the clipboard with random noise.
     */
    fun overwrite(context: Context) {
        val noise = ByteArray(NOISE_BYTES).also {
            SecureRandom().nextBytes(it)
        }.joinToString("") { byte -> "%02x".format(byte) }

        val clipboard =
            context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(LABEL_CLEARED, noise))

        clearRunnable = null
    }
}