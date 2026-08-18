package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MediBluePrimary,
    onPrimary = Color.White,
    primaryContainer = MediBlueContainer,
    onPrimaryContainer = MediOnBlueContainer,
    secondary = MediTeal,
    onSecondary = Color.White,
    secondaryContainer = MediTealContainer,
    background = Color(0xFFF5F7FA),
    onBackground = NeutralDark,
    surface = Color.White,
    onSurface = NeutralDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralMedium,
    outline = NeutralBorder
)

private val DarkColorScheme = LightColorScheme

@Composable
fun MediFindTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MediFindTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
