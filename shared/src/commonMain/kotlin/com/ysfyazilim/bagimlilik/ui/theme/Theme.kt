package com.opendrip.bagimlilik.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// Tema yönetimi için Local provider
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
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    val prefs = remember { context.getSharedPreferences("baksi_settings", Context.MODE_PRIVATE) }
    val isDark = remember { mutableStateOf(prefs.getBoolean("is_dark", systemDark)) }

    CompositionLocalProvider(LocalThemeIsDark provides isDark) {
        val colorScheme = if (isDark.value) DarkColorScheme else LightColorScheme
        
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
