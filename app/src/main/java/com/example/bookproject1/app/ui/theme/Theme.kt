package com.example.bookproject1.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// نظام ألوان الثيم الداكن (درجات البني الدافئ بدلاً من الأسود الحاد)
private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimaryDark,
    secondary = AmberSecondaryDark,
    background = BrownBackgroundDark,
    surface = BrownSurfaceDark,
    surfaceVariant = BrownSurfaceVariantDark,
    onPrimary = BrownBackgroundDark,
    onSecondary = BrownBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark
)

// نظام ألوان الثيم الفاتح (درجات الأوف وايت مع البني الأنيق)
private val LightColorScheme = lightColorScheme(
    primary = BrownPrimaryLight,
    secondary = BrownSecondaryLight,
    background = CreamBackgroundLight,
    surface = CreamSurfaceLight,
    surfaceVariant = CreamSurfaceVariantLight,
    onPrimary = CreamBackgroundLight,
    onSecondary = CreamBackgroundLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun AlMasabeehTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}