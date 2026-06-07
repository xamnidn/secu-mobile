package com.secu.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = """
                    SECU PASSWORD GENERATOR — PANDUAN PENGGUNAAN
                    
                    1. MASTER PASSWORD
                       Masukkan satu password kuat yang Anda hafalkan.
                       Password ini tidak disimpan di mana pun.
                    
                    2. SERVICE / WEBSITE
                       Masukkan nama layanan (misal: google.com).
                       Input akan dinormalisasi otomatis.
                    
                    3. VER (Rotation Version)
                       Mulai dari 1. Naikkan jika layanan meminta Anda
                       mengganti password tanpa mengubah Master Password.
                    
                    4. GENERATE PASSWORD
                       Tekan tombol untuk menghasilkan password.
                    
                    5. COPY
                       Salin password ke clipboard.
                       Clipboard akan otomatis dihapus setelah 10 detik.
                    
                    ADVANCED OPTIONS
                    - Character Set: pilih jenis karakter (A-Z, a-z, 0-9, simbol)
                    - Password Length: 8–128 karakter
                    - Bind to Device: kunci password ke perangkat ini
                    
                    KEAMANAN CLIPBOARD
                    SECU mengosongkan clipboard setelah 10 detik.
                    Nonaktifkan clipboard history di keyboard Anda.
                    
                    Password Anda adalah tanggung jawab Anda sendiri.
                """.trimIndent(),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }
    }
}