package com.example.maapanipusthakam.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.maapanipusthakam.MaaPaniApp

import androidx.compose.ui.graphics.Color

private val NotebookColorScheme = lightColorScheme(
    primary = BrickTerracotta,
    onPrimary = Color.White,
    primaryContainer = BrickTerracottaLight,
    onPrimaryContainer = BrickTerracottaDark,
    secondary = InkSecondary,
    onSecondary = Color.White,
    secondaryContainer = PaperCard,
    onSecondaryContainer = InkPrimary,
    background = PaperBackground,
    onBackground = InkPrimary,
    surface = PaperCard,
    onSurface = InkPrimary,
    surfaceVariant = PaperCardElevated,
    onSurfaceVariant = InkSecondary,
    outline = DividerColor,
    inverseSurface = Color(0xFF2C2724), // High contrast dark charcoal for Snackbars
    inverseOnSurface = Color(0xFFFFFDF9), // Crisp paper white for text on dark
    inversePrimary = ReceivedGreenLight   // Green action button for "మళ్లీ పెట్టు"
)

@Composable
fun MaaPaniPusthakamTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = NotebookColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PaperBackground.toArgb()
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    val isLargeText = try {
        MaaPaniApp.instance.isLargeTextEnabled
    } catch (e: Exception) {
        false
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(isLargeText),
        content = content
    )
}
