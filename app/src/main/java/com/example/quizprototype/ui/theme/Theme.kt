package com.example.quizprototype.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoadGreenLight,
    onPrimary = Coal,
    primaryContainer = RoadGreen,
    onPrimaryContainer = LaneWhite,
    secondary = DeepGold,
    onSecondary = Coal,
    secondaryContainer = SafetyAmber,
    onSecondaryContainer = LaneWhite,
    tertiary = SignBlue,
    onTertiary = LaneWhite,
    tertiaryContainer = Slate,
    onTertiaryContainer = LaneWhite,
    background = Coal,
    surface = ForestNight,
    onBackground = Mist,
    onSurface = Mist,
    surfaceVariant = Slate,
    onSurfaceVariant = LaneWhite.copy(alpha = 0.78f),
    outline = Moss
)

private val LightColorScheme = lightColorScheme(
    primary = RoadGreen,
    onPrimary = LaneWhite,
    primaryContainer = Color(0xFFDDEEE6),
    onPrimaryContainer = ForestNight,
    secondary = DeepGold,
    onSecondary = Coal,
    secondaryContainer = SignalSand,
    onSecondaryContainer = Coal,
    tertiary = SignBlue,
    onTertiary = LaneWhite,
    tertiaryContainer = Color(0xFFDCEBf6),
    onTertiaryContainer = Coal,
    background = Mist,
    surface = LaneWhite,
    onBackground = Coal,
    onSurface = Coal,
    surfaceVariant = Color(0xFFE3ECE7),
    onSurfaceVariant = Slate,
    outline = Moss
)

@Composable
fun QuizPrototypeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
