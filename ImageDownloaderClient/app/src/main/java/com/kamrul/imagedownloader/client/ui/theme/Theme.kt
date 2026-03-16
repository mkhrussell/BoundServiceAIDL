package com.kamrul.imagedownloader.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF22577A),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF38A3A5),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF102A43),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF102A43),
    surfaceVariant = Color(0xFFDCEAF2),
    onSurfaceVariant = Color(0xFF294C60),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF80CED7),
    onPrimary = Color(0xFF0B1F2A),
    secondary = Color(0xFF57CC99),
    onSecondary = Color(0xFF06261B),
    background = Color(0xFF0B1F2A),
    onBackground = Color(0xFFEAF4F4),
    surface = Color(0xFF102A43),
    onSurface = Color(0xFFEAF4F4),
    surfaceVariant = Color(0xFF1F3B4D),
    onSurfaceVariant = Color(0xFFB7D5E5),
)

@Composable
fun ImageDownloaderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) {
            _root_ide_package_.com.kamrul.imagedownloader.client.ui.theme.DarkColors
        } else {
            _root_ide_package_.com.kamrul.imagedownloader.client.ui.theme.LightColors
        },
        content = content
    )
}
