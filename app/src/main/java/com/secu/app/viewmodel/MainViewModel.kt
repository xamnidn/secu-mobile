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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
        val clearSeconds: Int = 0,
        val clearMasterTrigger: Int = 0,
        val error: String? = null
    )

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private var clearJob: Job? = null

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
                    val charset = CryptoEngine.buildCharset(upper, lower, numbers, symbols)

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
                        isGenerating = false,
                        clearSeconds = 10
                    )
                }
                startClearTimer()
            } catch (e: Exception) {
                _state.update {
                    it.copy(isGenerating = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun startClearTimer() {
        clearJob?.cancel()
        clearJob = viewModelScope.launch {
            while (_state.value.clearSeconds > 0) {
                delay(1000)
                _state.update { it.copy(clearSeconds = it.clearSeconds - 1) }
            }
            _state.update { it.copy(password = "", clearSeconds = 0, entropyBits = 0.0, clearMasterTrigger = it.clearMasterTrigger + 1) }
        }
    }

    fun clearPassword() {
        _state.update { it.copy(password = "", clearSeconds = 0, entropyBits = 0.0, clearMasterTrigger = it.clearMasterTrigger + 1) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun copyToClipboard() {
        val pwd = _state.value.password
        if (pwd.isNotEmpty()) ClipboardManager.copy(getApplication(), pwd)
    }
}
