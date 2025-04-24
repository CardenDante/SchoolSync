package com.mihs.schoolsync.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Red500,
    background = White,
    surface = White,
    onPrimary = White,
    onSecondary = White
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    secondary = Red200,
    background = DarkGray,
    surface = Black,
    onPrimary = Black,
    onSecondary = Black
)

@Composable
fun SchoolSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}