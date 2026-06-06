package com.secu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secu.app.ui.screens.AboutScreen
import com.secu.app.ui.screens.HelpScreen
import com.secu.app.ui.screens.MainScreen
import com.secu.app.ui.theme.SecuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("secu_ui_prefs", MODE_PRIVATE)
        val savedDarkMode = prefs.getBoolean("dark_mode", false)

        setContent {
            var darkMode by remember { mutableStateOf(savedDarkMode) }

            SecuTheme(darkTheme = darkMode) {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateHelp  = { navController.navigate("help") },
                            onNavigateAbout = { navController.navigate("about") },
                            darkMode        = darkMode,
                            onToggleDark    = { newValue ->
                                darkMode = newValue
                                prefs.edit().putBoolean("dark_mode", newValue).apply()
                            }
                        )
                    }
                    composable("help") {
                        HelpScreen(onBack = { navController.popBackStack() })
                    }
                    composable("about") {
                        AboutScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}