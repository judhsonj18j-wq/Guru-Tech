package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = IndigoPrimary,
        onPrimary = TextOnIndigo,
        primaryContainer = IndigoSecondary,
        onPrimaryContainer = RawTextPrimaryDark,
        secondary = IndigoLight,
        onSecondary = RawBackgroundDark,
        secondaryContainer = RawSurfaceCardElevatedDark,
        onSecondaryContainer = RawTextPrimaryDark,
        tertiary = EmeraldSuccess,
        onTertiary = RawBackgroundDark,
        tertiaryContainer = EmeraldBg,
        onTertiaryContainer = EmeraldLight,
        background = RawBackgroundDark,
        onBackground = RawTextPrimaryDark,
        surface = RawSurfaceDark,
        onSurface = RawTextPrimaryDark,
        surfaceVariant = RawSurfaceCardDark,
        onSurfaceVariant = RawTextSecondaryDark,
        outline = RawSurfaceCardBorderDark,
        error = RoseError,
        onError = RawTextPrimaryDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = IndigoPrimary,
        onPrimary = TextOnIndigo,
        primaryContainer = IndigoLight.copy(alpha = 0.2f),
        onPrimaryContainer = RawTextPrimaryLight,
        secondary = IndigoSecondary,
        onSecondary = RawBackgroundLight,
        secondaryContainer = RawSurfaceCardElevatedLight,
        onSecondaryContainer = RawTextPrimaryLight,
        tertiary = EmeraldSuccess,
        onTertiary = RawBackgroundLight,
        tertiaryContainer = EmeraldLight.copy(alpha = 0.2f),
        onTertiaryContainer = EmeraldDark,
        background = RawBackgroundLight,
        onBackground = RawTextPrimaryLight,
        surface = RawSurfaceLight,
        onSurface = RawTextPrimaryLight,
        surfaceVariant = RawSurfaceCardLight,
        onSurfaceVariant = RawTextSecondaryLight,
        outline = RawSurfaceCardBorderLight,
        error = RoseError,
        onError = TextOnIndigo,
    )

@Composable
fun EduSmartTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val palette = if (darkTheme) DarkPalette else LightPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = palette.background.toArgb()
                it.navigationBarColor = palette.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalEduSmartColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
