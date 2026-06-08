package com.secu.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secu.app.ui.theme.*
import com.secu.app.utils.ServiceNormalizer
import com.secu.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateHelp: () -> Unit,
    onNavigateAbout: () -> Unit,
    darkMode: Boolean,
    onToggleDark: (Boolean) -> Unit,
    vm: MainViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error
    val onSurface = MaterialTheme.colorScheme.onSurface
    val typography = MaterialTheme.typography

    var masterPassword  by remember { mutableStateOf("") }
    var showPassword    by remember { mutableStateOf(false) }
    var rawService      by remember { mutableStateOf("") }
    var rotationVersion by remember { mutableStateOf(1) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var menuExpanded    by remember { mutableStateOf(false) }
    var copied          by remember { mutableStateOf(false) }

    var useUpper   by remember { mutableStateOf(true) }
    var useLower   by remember { mutableStateOf(true) }
    var useNumbers by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var deviceBind by remember { mutableStateOf(false) }
    var pwLength   by remember { mutableStateOf(16) }

    val sheetState = rememberModalBottomSheetState()
    val showErrorDialog = state.error != null

    val preview = remember(rawService) {
        val p = ServiceNormalizer.preview(rawService)
        if (p.isNotEmpty() && p != rawService.trim().lowercase()) "Preview: $p" else ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "SECU",
                            tint = primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("secu.my.id", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = onSurface)
                            Text("Argon2id deterministic password generator", fontSize = 9.sp, color = onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Light", color = if (!darkMode) primary else onSurfaceVariant, fontWeight = if (!darkMode) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { menuExpanded = false; onToggleDark(false) }
                            )
                            DropdownMenuItem(
                                text = { Text("Dark", color = if (darkMode) primary else onSurfaceVariant, fontWeight = if (darkMode) FontWeight.Bold else FontWeight.Normal) },
                                onClick = { menuExpanded = false; onToggleDark(true) }
                            )
                            HorizontalDivider()
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
                    text = if (deviceBind) "Device key stored locally - locked to this machine" else "Portable mode - password reproducible on any device",
                    fontSize = 9.sp,
                    color = if (deviceBind) errorColor else onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
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
            Text("Master password", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = masterPassword,
                onValueChange = { masterPassword = it },
                placeholder = { Text("Type master password...", color = onSurfaceVariant) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(
                            text = if (showPassword) "Hide" else "Show",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showPassword) primary else onSurfaceVariant
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primary, unfocusedBorderColor = BorderColor)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Service / Website", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = onSurface)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("VER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    IconButton(
                        onClick = { if (rotationVersion > 1) rotationVersion-- },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Remove, contentDescription = "Decrease version", tint = primary) }
                    Text(
                        text = rotationVersion.toString(),
                        fontSize = 11.sp,
                        modifier = Modifier.width(32.dp),
                        color = onSurface,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = { if (rotationVersion < 999) rotationVersion++ },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Add, contentDescription = "Increase version", tint = primary) }
                }
            }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = rawService,
                onValueChange = { rawService = it },
                placeholder = { Text("e.g., google.com", color = onSurfaceVariant) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primary, unfocusedBorderColor = BorderColor)
            )
            AnimatedVisibility(visible = preview.isNotEmpty()) {
                Text(text = preview, fontSize = 9.sp, color = onSurfaceVariant, fontStyle = FontStyle.Italic, modifier = Modifier.padding(start = 4.dp, top = 2.dp))
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.generate(masterPassword, rawService, rotationVersion, deviceBind, pwLength, useUpper, useLower, useNumbers, useSymbols) },
                enabled = !state.isGenerating,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(4.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) { Text(if (state.isGenerating) "Generating..." else "Generate password", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimary) }
            Spacer(Modifier.height(16.dp))

            Text("Your password", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = onSurface)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primary),
                trailingIcon = {
                    if (state.password.isNotEmpty()) {
                        TextButton(
                            onClick = { vm.copyToClipboard(); copied = true }
                        ) {
                            Text(text = if (copied && state.clearSeconds >= 0) "Copied" else "Copy", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (copied && state.clearSeconds >= 0) SuccessFg else primary)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primary, unfocusedBorderColor = primary)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (state.entropyBits > 0) strengthLabel(state.entropyBits) else "Estimated Entropy: -",
                    fontSize = 8.sp,
                    color = strengthColor(state.entropyBits, primary, errorColor),
                    fontStyle = FontStyle.Italic
                )
                if (state.clearSeconds > 0) {
                    Text("Clears in ${state.clearSeconds} s", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = errorColor)
                }
            }
            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { showBottomSheet = true }, modifier = Modifier.align(Alignment.End)) {
                Text("Advanced Options", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = primary)
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

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { vm.clearError() },
            title = { Text("Error") },
            text = { Text(state.error ?: "") },
            confirmButton = {
                TextButton(onClick = { vm.clearError() }) { Text("OK") }
            }
        )
    }
}

@Composable
fun AdvancedOptionsPanel(
    useUpper: Boolean, onUpperChange: (Boolean) -> Unit,
    useLower: Boolean, onLowerChange: (Boolean) -> Unit,
    useNumbers: Boolean, onNumbersChange: (Boolean) -> Unit,
    useSymbols: Boolean, onSymbolsChange: (Boolean) -> Unit,
    deviceBind: Boolean, onDeviceChange: (Boolean) -> Unit,
    pwLength: Int, onLengthChange: (Int) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error
    val onSurface = MaterialTheme.colorScheme.onSurface

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Advanced Options", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = onSurface)
            Spacer(Modifier.height(12.dp))
            Text("Character Parameters", fontSize = 9.sp, color = onSurfaceVariant, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = useUpper, onClick = { onUpperChange(!useUpper) }, label = { Text("A-Z", fontSize = 10.sp) })
                FilterChip(selected = useLower, onClick = { onLowerChange(!useLower) }, label = { Text("a-z", fontSize = 10.sp) })
                FilterChip(selected = useNumbers, onClick = { onNumbersChange(!useNumbers) }, label = { Text("0-9", fontSize = 10.sp) })
                FilterChip(selected = useSymbols, onClick = { onSymbolsChange(!useSymbols) }, label = { Text("!@#\$", fontSize = 10.sp) })
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Password Length", fontSize = 9.sp, color = onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Text("(8\u2013128)", fontSize = 8.sp, color = onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (pwLength > 8) onLengthChange(pwLength - 1) },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Remove, contentDescription = "Decrease length", tint = primary) }
                    Text(text = pwLength.toString(), fontSize = 11.sp, modifier = Modifier.width(32.dp), color = onSurface, textAlign = TextAlign.Center)
                    IconButton(
                        onClick = { if (pwLength < 128) onLengthChange(pwLength + 1) },
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Add, contentDescription = "Increase length", tint = primary) }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bind to Device", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (deviceBind) errorColor else onSurfaceVariant)
                    Text("Locks password to this device only", fontSize = 8.sp, color = onSurfaceVariant)
                }
                Checkbox(
                    checked = deviceBind,
                    onCheckedChange = onDeviceChange,
                    colors = CheckboxDefaults.colors(checkedColor = errorColor, uncheckedColor = onSurfaceVariant)
                )
            }
        }
    }
}

fun strengthLabel(bits: Double): String = when {
    bits >= 128 -> "Est. Entropy: ${bits.toInt()} bits (Very Strong)"
    bits >= 80  -> "Est. Entropy: ${bits.toInt()} bits (Strong)"
    bits >= 50  -> "Est. Entropy: ${bits.toInt()} bits (Medium)"
    else        -> "Est. Entropy: ${bits.toInt()} bits (Weak)"
}

fun strengthColor(bits: Double, primary: androidx.compose.ui.graphics.Color, errorColor: androidx.compose.ui.graphics.Color): androidx.compose.ui.graphics.Color = when {
    bits >= 128 -> VeryStrong
    bits >= 80  -> SuccessFg
    bits >= 50  -> primary
    else        -> errorColor
}
