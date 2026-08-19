package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = AccentContainerDark,
    onPrimaryContainer = Color(0xFFF3E8FF),
    inversePrimary = AccentPurple,

    secondary = TextSecondaryDark,
    onSecondary = CanvasDark,
    secondaryContainer = SurfaceContainerDark,
    onSecondaryContainer = TextPrimaryDark,

    tertiary = AccentPurple,
    onTertiary = Color.White,
    tertiaryContainer = AccentContainerDark,
    onTertiaryContainer = Color(0xFFF3E8FF),

    background = CanvasDark,
    onBackground = TextPrimaryDark,

    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceContainerDark,
    onSurfaceVariant = TextSecondaryDark,

    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = StatusRed,
    onError = Color(0xFF450A0A),
    errorContainer = StatusRedBg,
    onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
