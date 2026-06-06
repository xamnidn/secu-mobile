package com.secu.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.secu.app.data.ClipboardManager
import com.secu.app.data.DeviceIdManager
import com.secu.app.data.ServiceNormalizer
import com.secu.app.domain.CryptoEngine
import com.secu.app.domain.SaltBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ln

/**
 * ViewModel acting as the integration hub for all domain and data modules.
 * Exposes a single [MainUiState] flow consumed by [MainScreen].
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    data class MainUiState(
        val password: String = "",
        val entropyBits: Double = 0.0,
        val isGenerating: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    /**
     * Generates a deterministic password. Called directly from [MainScreen].
     *
     * @param masterPassword  The master password (never stored).
     * @param rawService      Raw service/website name (will be normalized).
     * @param rotationVersion Rotation version (1–999).
     * @param deviceBind      If true, includes device ID in salt.
     * @param length          Desired password length (8–128).
     * @param upper           Include uppercase A–Z.
     * @param lower           Include lowercase a–z.
     * @param numbers         Include digits 0–9.
     * @param symbols         Include symbols !@#... etc.
     */
    fun generate(
        masterPassword: String,
        rawService: String,
        rotationVersion: Int,
        deviceBind: Boolean,
        length: Int,
        upper: Boolean,
        lower: Boolean,
        numbers: Boolean,
        symbols: Boolean
    ) {
        // Reset error and set loading immediately
        _state.update { it.copy(isGenerating = true, error = null, password = "") }

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    // 1. Normalize service name
                    val normalizedService = ServiceNormalizer.normalize(rawService)

                    // 2. Build character set from booleans
                    val charset = buildCharset(upper, lower, numbers, symbols)

                    // 3. Build salt (with optional device component)
                    val deviceComponent = if (deviceBind) {
                        DeviceIdManager.getDeviceId(getApplication())
                    } else {
                        ""
                    }
                    val salt = SaltBuilder.build(normalizedService, deviceComponent)

                    // 4. Derive password
                    val password = CryptoEngine.derivePassword(masterPassword, salt, charset, length)

                    // 5. Calculate entropy
                    val entropy = length * (ln(charset.length.toDouble()) / ln(2.0))

                    Pair(password, entropy)
                }

                _state.update {
                    it.copy(
                        password = result.first,
                        entropyBits = result.second,
                        isGenerating = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isGenerating = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    /**
     * Clears the generated password from state (auto-clear after countdown).
     */
    fun clearPassword() {
        _state.update { it.copy(password = "", error = null, entropyBits = 0.0) }
    }

    /**
     * Clears the current error message.
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * Copies the current password to clipboard (called from UI).
     */
    fun copyToClipboard() {
        val pwd = _state.value.password
        if (pwd.isNotEmpty()) {
            ClipboardManager.copy(getApplication(), pwd)
        }
    }

    /**
     * Builds a character set string from individual flags.
     * Must match desktop implementation exactly.
     */
    private fun buildCharset(
        upper: Boolean,
        lower: Boolean,
        numbers: Boolean,
        symbols: Boolean
    ): String {
        val sb = StringBuilder()
        if (upper)   sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        if (lower)   sb.append("abcdefghijklmnopqrstuvwxyz")
        if (numbers) sb.append("0123456789")
        if (symbols) sb.append("!@#$%^&*()_+-=[]{}|;:,.<>?/~")
        require(sb.isNotEmpty()) { "At least one character set must be selected" }
        return sb.toString()
    }
}