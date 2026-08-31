package com.ysfyazilim.bagimlilik.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// Tema yönetimi için KMP Uyumlu Local provider
val LocalThemeIsDark = compositionLocalOf<MutableState<Boolean>> { error("No state found") }

private val DarkColorScheme = darkColorScheme(
    primary = BaksiCyan,
    secondary = LightPurple,
    tertiary = BaksiLightGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onBackground = DarkText,
    onSurface = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = BaksiGreen,
    secondary = RoyalPurple,
    tertiary = BaksiCyan,
    background = BackgroundWhite,
    surface = SurfaceWhite,
    onPrimary = SurfaceWhite,
    onSecondary = SurfaceWhite,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun BagimlilikTheme(
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = remember { mutableStateOf(systemDark) }

    CompositionLocalProvider(LocalThemeIsDark provides isDark) {
        val colorScheme = if (isDark.value) DarkColorScheme else LightColorScheme
        
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}