package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ================= RAW DARK VALUES =================
val RawBackgroundDark = Color(0xFF0A0E1A)
val RawSurfaceDark = Color(0xFF0F172A)
val RawSurfaceCardDark = Color(0xFF172033)
val RawSurfaceCardElevatedDark = Color(0xFF1E293B)
val RawSurfaceCardBorderDark = Color(0xFF2E3D5B)
val RawTextPrimaryDark = Color(0xFFF8FAFC)
val RawTextSecondaryDark = Color(0xFF94A3B8)
val RawTextMutedDark = Color(0xFF64748B)

// ================= RAW LIGHT VALUES =================
val RawBackgroundLight = Color(0xFFF1F5F9)
val RawSurfaceLight = Color(0xFFFFFFFF)
val RawSurfaceCardLight = Color(0xFFFFFFFF)
val RawSurfaceCardElevatedLight = Color(0xFFE2E8F0)
val RawSurfaceCardBorderLight = Color(0xFFCBD5E1)
val RawTextPrimaryLight = Color(0xFF0F172A)
val RawTextSecondaryLight = Color(0xFF475569)
val RawTextMutedLight = Color(0xFF64748B)

// ================= BRAND ACCENTS =================
val IndigoPrimary = Color(0xFF6366F1)
val IndigoSecondary = Color(0xFF4F46E5)
val IndigoLight = Color(0xFF818CF8)
val IndigoDark = Color(0xFF3730A3)

// Success / Emerald States
val EmeraldSuccess = Color(0xFF10B981)
val EmeraldDark = Color(0xFF059669)
val EmeraldLight = Color(0xFF34D399)
val EmeraldBg = Color(0xFF064E3B)

// Warning & Error Accents
val AmberWarning = Color(0xFFF59E0B)
val RoseError = Color(0xFFF43F5E)
val SkyInfo = Color(0xFF38BDF8)
val PurpleAccent = Color(0xFFA855F7)
val TextOnIndigo = Color(0xFFFFFFFF)

data class EduSmartColorPalette(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceCard: Color,
    val surfaceCardElevated: Color,
    val surfaceCardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color = IndigoPrimary,
    val primaryLight: Color = IndigoLight,
    val primaryDark: Color = IndigoDark,
    val success: Color = EmeraldSuccess,
    val successLight: Color = EmeraldLight,
    val warning: Color = AmberWarning,
    val error: Color = RoseError,
    val info: Color = SkyInfo
)

val DarkPalette = EduSmartColorPalette(
    isDark = true,
    background = RawBackgroundDark,
    surface = RawSurfaceDark,
    surfaceCard = RawSurfaceCardDark,
    surfaceCardElevated = RawSurfaceCardElevatedDark,
    surfaceCardBorder = RawSurfaceCardBorderDark,
    textPrimary = RawTextPrimaryDark,
    textSecondary = RawTextSecondaryDark,
    textMuted = RawTextMutedDark
)

val LightPalette = EduSmartColorPalette(
    isDark = false,
    background = RawBackgroundLight,
    surface = RawSurfaceLight,
    surfaceCard = RawSurfaceCardLight,
    surfaceCardElevated = RawSurfaceCardElevatedLight,
    surfaceCardBorder = RawSurfaceCardBorderLight,
    textPrimary = RawTextPrimaryLight,
    textSecondary = RawTextSecondaryLight,
    textMuted = RawTextMutedLight
)

val LocalEduSmartColors = staticCompositionLocalOf { DarkPalette }

object AppTheme {
    val colors: EduSmartColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalEduSmartColors.current
}

// ================= DYNAMIC COMPOSE GETTERS =================
// These properties automatically resolve according to the active theme (Dark vs Light)

val BackgroundDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.background

val SurfaceDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.surface

val SurfaceCard: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.surfaceCard

val SurfaceCardElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.surfaceCardElevated

val SurfaceCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.surfaceCardBorder

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalEduSmartColors.current.textMuted
