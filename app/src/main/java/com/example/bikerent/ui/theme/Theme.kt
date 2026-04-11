package com.example.bikerent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Green800,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Green900,
    background = BackgroundGray,
    surface = androidx.compose.ui.graphics.Color.White,
    onBackground = DarkBackground,
    onSurface = DarkBackground,
)

@Composable
fun BikeRentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
