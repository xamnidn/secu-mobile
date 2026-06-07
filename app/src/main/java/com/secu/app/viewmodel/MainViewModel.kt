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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    data class MainUiState(
        val password: String = "",
        val entropyBits: Double = 0.0,
        val isGenerating: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

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
        _state.update { it.copy(isGenerating = true, error = null, password = "") }

        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    val normalizedService = ServiceNormalizer.normalize(rawService)
                    val charset = buildCharset(upper, lower, numbers, symbols)

                    val deviceComponent = if (deviceBind) {
                        DeviceIdManager.getDeviceId(getApplication())
                    } else ""

                    val salt = SaltBuilder.build(normalizedService, deviceComponent, rotationVersion)
                    val password = CryptoEngine.derivePassword(masterPassword, salt, charset, length)
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
                    it.copy(isGenerating = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun clearPassword() {
        _state.update { it.copy(password = "", error = null, entropyBits = 0.0) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun copyToClipboard() {
        val pwd = _state.value.password
        if (pwd.isNotEmpty()) ClipboardManager.copy(getApplication(), pwd)
    }

    private fun buildCharset(upper: Boolean, lower: Boolean, numbers: Boolean, symbols: Boolean): String {
        val sb = StringBuilder()
        if (upper) sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        if (lower) sb.append("abcdefghijklmnopqrstuvwxyz")
        if (numbers) sb.append("0123456789")
        if (symbols) sb.append("!@#%^&*()_+-=[]{}|;:,.<>?/~")
        require(sb.isNotEmpty()) { "At least one character set must be selected" }
        return sb.toString()
    }
}
