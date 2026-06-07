package com.secu.app.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secu.app.ui.theme.*
import com.secu.app.utils.AppClipboardManager
import com.secu.app.utils.ServiceNormalizer
import com.secu.app.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateHelp: () -> Unit,
    onNavigateAbout: () -> Unit,
    darkMode: Boolean,
    onToggleDark: (Boolean) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    var masterPassword    by remember { mutableStateOf("") }
    var showPassword      by remember { mutableStateOf(false) }
    var rawService        by remember { mutableStateOf("") }
    var rotationVersion   by remember { mutableStateOf(1) }
    var showBottomSheet   by remember { mutableStateOf(false) }
    var menuExpanded      by remember { mutableStateOf(false) }
    var countdown         by remember { mutableStateOf(0) }
    var copied            by remember { mutableStateOf(false) }

    var useUpper    by remember { mutableStateOf(true) }
    var useLower    by remember { mutableStateOf(true) }
    var useNumbers  by remember { mutableStateOf(true) }
    var useSymbols  by remember { mutableStateOf(true) }
    var deviceBind  by remember { mutableStateOf(false) }
    var pwLength    by remember { mutableStateOf(16f) }

    val sheetState = rememberModalBottomSheetState()

    val preview = remember(rawService) {
        val p = ServiceNormalizer.preview(rawService)
        if (p.isNotEmpty() && p != rawService.trim().lowercase()) "Preview: p" else ""
    }

    LaunchedEffect(state.password) {
        if (state.password.isNotEmpty()) {
            countdown = 10
            copied = false
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            vm.clearPassword()
            AppClipboardManager.overwrite(context)
            masterPassword = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SECU PASSWORD GENERATOR", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Secure argon2id-based deterministic password generator", fontSize = 9.sp, color = TextMuted)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Light", fontSize = 14.sp, color = if (!darkMode) AccentBlue else TextMuted, fontWeight = if (!darkMode) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { menuExpanded = false; onToggleDark(false) }
                            )
                            DropdownMenuItem(
                                text = { Text("Dark", fontSize = 14.sp, color = if (darkMode) AccentBlue else TextMuted, fontWeight = if (darkMode) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { menuExpanded = false; onToggleDark(true) }
                            )
                            Divider()
                            DropdownMenuItem(text = { Text("Help") }, onClick = { menuExpanded = false; onNavigateHelp() })
                            DropdownMenuItem(text = { Text("About") }, onClick = { menuExpanded = false; onNavigateAbout() })
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface) {
                Text(
                    text = if (deviceBind) "Device key stored locally — locked to this machine" else "Portable mode — password reproducible on any device",
                    fontSize = 9.sp,
                    color = if (deviceBind) WarningColor else TextMuted,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))
            Text("MASTER PASSWORD", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = masterPassword,
                onValueChange = { masterPassword = it },
                placeholder = { Text("Type master password...", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(
                            text = if (showPassword) "HIDE" else "SHOW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showPassword) AccentBlue else TextMuted
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SERVICE / WEBSITE", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = { if (rotationVersion > 1) rotationVersion-- }, modifier = Modifier.size(24.dp)) { Text("-", color = AccentBlue, fontWeight = FontWeight.Bold) }
                    Text(text = rotationVersion.toString(), fontSize = 11.sp, modifier = Modifier.width(28.dp), color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = { if (rotationVersion < 999) rotationVersion++ }, modifier = Modifier.size(24.dp)) { Text("+", color = AccentBlue, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = rawService,
                onValueChange = { rawService = it },
                placeholder = { Text("e.g., google.com", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = BorderColor)
            )
            AnimatedVisibility(visible = preview.isNotEmpty()) {
                Text(text = preview, fontSize = 9.sp, color = TextMuted, fontStyle = FontStyle.Italic, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
            }
            Spacer(Modifier.height(16.dp))

            state.error?.let { err ->
                Card(colors = CardDefaults.cardColors(containerColor = WarningColor.copy(alpha = 0.1f)), modifier = Modifier.fillMaxWidth()) {
                    Text(text = err, color = WarningColor, fontSize = 10.sp, modifier = Modifier.padding(12.dp))
                }
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = { vm.generate(masterPassword, rawService, rotationVersion, deviceBind, pwLength.toInt(), useUpper, useLower, useNumbers, useSymbols) },
                enabled = !state.isGenerating,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) { Text(if (state.isGenerating) "GENERATING..." else "GENERATE PASSWORD", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White) }
            Spacer(Modifier.height(16.dp))

            Text("YOUR PASSWORD", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentBlue),
                trailingIcon = {
                    if (state.password.isNotEmpty()) {
                        TextButton(onClick = { AppClipboardManager.copy(context, state.password); copied = true; countdown = 10 }) {
                            Text(text = if (copied) "COPIED" else "COPY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (copied) SuccessFg else AccentBlue)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue, unfocusedBorderColor = AccentBlue)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(if (state.entropyBits > 0) strengthLabel(state.entropyBits) else "Estimated Entropy: -", fontSize = 8.sp, color = strengthColor(state.entropyBits), fontStyle = FontStyle.Italic)
                if (countdown > 0) Text("Clears in countdown s", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = WarningColor)
            }
            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { showBottomSheet = true }, modifier = Modifier.align(Alignment.End)) {
                Text("Advanced Options", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            AdvancedOptionsPanel(
                useUpper = useUpper, onUpperChange = { useUpper = it },
                useLower = useLower, onLowerChange = { useLower = it },
                useNumbers = useNumbers, onNumbersChange = { useNumbers = it },
                useSymbols = useSymbols, onSymbolsChange = { useSymbols = it },
                deviceBind = deviceBind, onDeviceChange = { deviceBind = it },
                pwLength = pwLength, onLengthChange = { pwLength = it }
            )
        }
    }
}

@Composable
fun AdvancedOptionsPanel(
    useUpper: Boolean, onUpperChange: (Boolean) -> Unit,
    useLower: Boolean, onLowerChange: (Boolean) -> Unit,
    useNumbers: Boolean, onNumbersChange: (Boolean) -> Unit,
    useSymbols: Boolean, onSymbolsChange: (Boolean) -> Unit,
    deviceBind: Boolean, onDeviceChange: (Boolean) -> Unit,
    pwLength: Float, onLengthChange: (Float) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("ADVANCED OPTIONS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            Text("CHARACTER SET", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = useUpper, onClick = { onUpperChange(!useUpper) }, label = { Text("A-Z", fontSize = 10.sp) })
                FilterChip(selected = useLower, onClick = { onLowerChange(!useLower) }, label = { Text("a-z", fontSize = 10.sp) })
                FilterChip(selected = useNumbers, onClick = { onNumbersChange(!useNumbers) }, label = { Text("0-9", fontSize = 10.sp) })
                FilterChip(selected = useSymbols, onClick = { onSymbolsChange(!useSymbols) }, label = { Text("!@#", fontSize = 10.sp) })
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("PASSWORD LENGTH", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Text(text = pwLength.toInt().toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
            }
            Slider(value = pwLength, onValueChange = onLengthChange, valueRange = 8f..128f, steps = 119, colors = SliderDefaults.colors(thumbColor = AccentBlue, activeTrackColor = AccentBlue))
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("BIND TO DEVICE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (deviceBind) WarningColor else TextMuted)
                    Text("Locks password to this device only", fontSize = 8.sp, color = TextMuted)
                }
                Switch(checked = deviceBind, onCheckedChange = onDeviceChange, colors = SwitchDefaults.colors(checkedThumbColor = WarningColor))
            }
        }
    }
}

fun strengthLabel(bits: Double): String = when {
    bits >= 128 -> "Est. Entropy: {bits.toInt()} bits (Very Strong)"
    bits >= 80  -> "Est. Entropy: {bits.toInt()} bits (Strong)"
    bits >= 50  -> "Est. Entropy: {bits.toInt()} bits (Medium)"
    else        -> "Est. Entropy: {bits.toInt()} bits (Weak)"
}

fun strengthColor(bits: Double): Color = when {
    bits >= 128 -> VeryStrong
    bits >= 80  -> SuccessFg
    bits >= 50  -> AccentBlue
    else        -> WarningColor
}
