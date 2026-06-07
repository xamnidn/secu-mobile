package com.secu.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SECU Password Generator", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text("v1.3.0", fontSize = 12.sp)
            Spacer(Modifier.height(16.dp))
            
            Text("PARAMETER KRIPTOGRAFI", fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = """
                    Algoritma: Argon2id
                    Time cost: 3 iterasi
                    Memory: 65.536 KB (64 MB)
                    Parallelism: 4 thread
                    Hash length: 32 byte (256-bit)
                    Ekspansi: SHA-256 + rejection sampling
                    Profil: OWASP Interactive Login
                    
                    FORMAT SALT
                    Portable: v1.3.0::google.com:1
                    Device-bound: v1.3.0:<device_id>:google.com:1
                    
                    LISENSI MIT
                    Perangkat lunak ini disediakan "sebagaimana adanya",
                    tanpa jaminan apa pun.
                """.trimIndent(),
                fontSize = 10.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }
    }
}