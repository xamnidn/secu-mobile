package com.secu.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HelpSection("Password Generation") {
                HelpBullet("Enter your master password (memorized only, never stored)")
                HelpBullet("Enter service name (e.g., google.com) - auto-normalized")
                HelpBullet("Set VER (rotation version) - start at 1, increment when a site requires a new password")
                HelpBullet("Press Generate - deterministic output based solely on your inputs")
                HelpBullet("Copy password to clipboard - auto-cleared after 10 seconds")
            }

            HelpSection("Advanced Options") {
                HelpBullet("Character Set: select which character types to include (A-Z, a-z, 0-9, symbols)")
                HelpBullet("Password Length: set between 8-128 characters")
                HelpBullet("Bind to Device: lock password to this specific device using Android Keystore")
            }

            HelpSection("Clipboard Security") {
                HelpBullet("Passwords are automatically removed from clipboard after 10 seconds")
                HelpBullet("Clipboard is overwritten with random data after expiry")
                HelpBullet("Disable clipboard history in your keyboard settings for added security")
                HelpBullet("Other apps on the same device can read clipboard during the 10-second window")
            }

            HelpSection("Important Notes") {
                HelpBullet("SECU is 100% offline - no network access required or permitted")
                HelpBullet("No passwords are ever stored or transmitted")
                HelpBullet("Losing your master password means permanent loss of all generated passwords")
                HelpBullet("Cross-platform compatible: same inputs produce identical passwords on SECU desktop (Windows/Linux)")
            }
        }
    }
}

@Composable
private fun HelpSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            content()
        }
    }
}

@Composable
private fun HelpBullet(text: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "\u2022  ",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 14.sp
        )
    }
}
