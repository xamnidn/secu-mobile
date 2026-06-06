package com.secu.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    background = BgMain,
    surface = BgCard,
    onBackground = TextMain,
    onSurface = TextMain,
    primary = AccentBlue,
    onPrimary = Color.White,
    error = WarningColor,
)

private val DarkColors = darkColorScheme(
    background = DarkBgMain,
    surface = DarkBgCard,
    onBackground = DarkTextMain,
    onSurface = DarkTextMain,
    primary = AccentBlue,
    onPrimary = Color.White,
    error = WarningColor,
)

@Composable
fun SecuTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SecuTypography,
        content = content
    )
}