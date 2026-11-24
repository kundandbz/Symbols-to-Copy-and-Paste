package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.AppColorPalette
import com.example.data.AppThemeMode

private fun buildStudioLightScheme(
  primary: Color,
  primaryContainer: Color,
  onPrimaryContainer: Color,
  secondary: Color,
  secondaryContainer: Color,
  onSecondaryContainer: Color,
  tertiary: Color,
  tertiaryContainer: Color,
  onTertiaryContainer: Color,
): ColorScheme = lightColorScheme(
  primary = primary,
  onPrimary = Color.White,
  primaryContainer = primaryContainer,
  onPrimaryContainer = onPrimaryContainer,
  secondary = secondary,
  onSecondary = Color.White,
  secondaryContainer = secondaryContainer,
  onSecondaryContainer = onSecondaryContainer,
  tertiary = tertiary,
  onTertiary = Color.White,
  tertiaryContainer = tertiaryContainer,
  onTertiaryContainer = onTertiaryContainer,
  background = StudioBackgroundLight,
  onBackground = StudioOnBackgroundLight,
  surface = StudioSurfaceLight,
  onSurface = StudioOnSurfaceLight,
  surfaceVariant = StudioSurfaceVariantLight,
  onSurfaceVariant = StudioOnSurfaceVariantLight,
  outline = StudioOutlineLight,
  outlineVariant = StudioOutlineVariantLight,
  surfaceContainerLowest = StudioSurfaceContainerLowestLight,
  surfaceContainerLow = StudioSurfaceContainerLowLight,
  surfaceContainer = StudioSurfaceContainerLight,
  surfaceContainerHigh = StudioSurfaceContainerHighLight,
)

private fun buildStudioDarkScheme(
  primary: Color,
  onPrimary: Color,
  primaryContainer: Color,
  onPrimaryContainer: Color,
  secondary: Color,
  onSecondary: Color,
  secondaryContainer: Color,
  onSecondaryContainer: Color,
  tertiary: Color,
  onTertiary: Color,
  tertiaryContainer: Color,
  onTertiaryContainer: Color,
): ColorScheme = darkColorScheme(
  primary = primary,
  onPrimary = onPrimary,
  primaryContainer = primaryContainer,
  onPrimaryContainer = onPrimaryContainer,
  secondary = secondary,
  onSecondary = onSecondary,
  secondaryContainer = secondaryContainer,
  onSecondaryContainer = onSecondaryContainer,
  tertiary = tertiary,
  onTertiary = onTertiary,
  tertiaryContainer = tertiaryContainer,
  onTertiaryContainer = onTertiaryContainer,
  background = StudioBackgroundDark,
  onBackground = StudioOnBackgroundDark,
  surface = StudioSurfaceDark,
  onSurface = StudioOnSurfaceDark,
  surfaceVariant = StudioSurfaceVariantDark,
  onSurfaceVariant = StudioOnSurfaceVariantDark,
  outline = StudioOutlineDark,
  outlineVariant = StudioOutlineVariantDark,
  surfaceContainerLowest = StudioSurfaceContainerLowestDark,
  surfaceContainerLow = StudioSurfaceContainerLowDark,
  surfaceContainer = StudioSurfaceContainerDark,
  surfaceContainerHigh = StudioSurfaceContainerHighDark,
)

private val IndigoLightScheme = buildStudioLightScheme(
  primary = StudioPrimaryLight,
  primaryContainer = StudioPrimaryContainerLight,
  onPrimaryContainer = StudioOnPrimaryContainerLight,
  secondary = StudioSecondaryLight,
  secondaryContainer = StudioSecondaryContainerLight,
  onSecondaryContainer = StudioOnSecondaryContainerLight,
  tertiary = StudioTertiaryLight,
  tertiaryContainer = StudioTertiaryContainerLight,
  onTertiaryContainer = StudioOnTertiaryContainerLight,
)

private val IndigoDarkScheme = buildStudioDarkScheme(
  primary = StudioPrimaryDark,
  onPrimary = StudioOnPrimaryDark,
  primaryContainer = StudioPrimaryContainerDark,
  onPrimaryContainer = StudioOnPrimaryContainerDark,
  secondary = StudioSecondaryDark,
  onSecondary = StudioOnSecondaryDark,
  secondaryContainer = StudioSecondaryContainerDark,
  onSecondaryContainer = StudioOnSecondaryContainerDark,
  tertiary = StudioTertiaryDark,
  onTertiary = StudioOnTertiaryDark,
  tertiaryContainer = StudioTertiaryContainerDark,
  onTertiaryContainer = StudioOnTertiaryContainerDark,
)

private val EmeraldLightScheme = buildStudioLightScheme(
  primary = Color(0xFF047857),
  primaryContainer = Color(0xFFD1FAE5),
  onPrimaryContainer = Color(0xFF064E3B),
  secondary = Color(0xFF0F766E),
  secondaryContainer = Color(0xFFCCFBF1),
  onSecondaryContainer = Color(0xFF134E4A),
  tertiary = Color(0xFFD97706),
  tertiaryContainer = Color(0xFFFEF3C7),
  onTertiaryContainer = Color(0xFF78350F),
)

private val EmeraldDarkScheme = buildStudioDarkScheme(
  primary = Color(0xFF6EE7B7),
  onPrimary = Color(0xFF022C22),
  primaryContainer = Color(0xFF065F46),
  onPrimaryContainer = Color(0xFFD1FAE5),
  secondary = Color(0xFF5EEAD4),
  onSecondary = Color(0xFF042F2E),
  secondaryContainer = Color(0xFF115E59),
  onSecondaryContainer = Color(0xFFCCFBF1),
  tertiary = Color(0xFFFBBF24),
  onTertiary = Color(0xFF451A03),
  tertiaryContainer = Color(0xFF78350F),
  onTertiaryContainer = Color(0xFFFEF3C7),
)

private val RoseLightScheme = buildStudioLightScheme(
  primary = Color(0xFFBE123C),
  primaryContainer = Color(0xFFFFE4E6),
  onPrimaryContainer = Color(0xFF4C0519),
  secondary = Color(0xFF9D174D),
  secondaryContainer = Color(0xFFFCE7F3),
  onSecondaryContainer = Color(0xFF500724),
  tertiary = Color(0xFFB45309),
  tertiaryContainer = Color(0xFFFEF3C7),
  onTertiaryContainer = Color(0xFF451A03),
)

private val RoseDarkScheme = buildStudioDarkScheme(
  primary = Color(0xFFFDA4AF),
  onPrimary = Color(0xFF4C0519),
  primaryContainer = Color(0xFF881337),
  onPrimaryContainer = Color(0xFFFFE4E6),
  secondary = Color(0xFFF9A8D4),
  onSecondary = Color(0xFF500724),
  secondaryContainer = Color(0xFF831843),
  onSecondaryContainer = Color(0xFFFCE7F3),
  tertiary = Color(0xFFFCD34D),
  onTertiary = Color(0xFF451A03),
  tertiaryContainer = Color(0xFF78350F),
  onTertiaryContainer = Color(0xFFFEF3C7),
)

private val AmberLightScheme = buildStudioLightScheme(
  primary = Color(0xFFB45309),
  primaryContainer = Color(0xFFFEF3C7),
  onPrimaryContainer = Color(0xFF451A03),
  secondary = Color(0xFFC2410C),
  secondaryContainer = Color(0xFFFFEDD5),
  onSecondaryContainer = Color(0xFF431407),
  tertiary = Color(0xFF0F766E),
  tertiaryContainer = Color(0xFFCCFBF1),
  onTertiaryContainer = Color(0xFF134E4A),
)

private val AmberDarkScheme = buildStudioDarkScheme(
  primary = Color(0xFFFBBF24),
  onPrimary = Color(0xFF451A03),
  primaryContainer = Color(0xFF78350F),
  onPrimaryContainer = Color(0xFFFEF3C7),
  secondary = Color(0xFFFB923C),
  onSecondary = Color(0xFF431407),
  secondaryContainer = Color(0xFF7C2D12),
  onSecondaryContainer = Color(0xFFFFEDD5),
  tertiary = Color(0xFF5EEAD4),
  onTertiary = Color(0xFF042F2E),
  tertiaryContainer = Color(0xFF115E59),
  onTertiaryContainer = Color(0xFFCCFBF1),
)

private val OceanLightScheme = buildStudioLightScheme(
  primary = Color(0xFF0369A1),
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0C4A6E),
  secondary = Color(0xFF0E7490),
  secondaryContainer = Color(0xFFCFFAFE),
  onSecondaryContainer = Color(0xFF164E63),
  tertiary = Color(0xFF6D28D9),
  tertiaryContainer = Color(0xFFEDE9FE),
  onTertiaryContainer = Color(0xFF3B0764),
)

private val OceanDarkScheme = buildStudioDarkScheme(
  primary = Color(0xFF7DD3FC),
  onPrimary = Color(0xFF082F49),
  primaryContainer = Color(0xFF075985),
  onPrimaryContainer = Color(0xFFE0F2FE),
  secondary = Color(0xFF67E8F9),
  onSecondary = Color(0xFF083344),
  secondaryContainer = Color(0xFF155E75),
  onSecondaryContainer = Color(0xFFCFFAFE),
  tertiary = Color(0xFFC4B5FD),
  onTertiary = Color(0xFF2E1065),
  tertiaryContainer = Color(0xFF5B21B6),
  onTertiaryContainer = Color(0xFFEDE9FE),
)

private val VioletLightScheme = buildStudioLightScheme(
  primary = Color(0xFF6D28D9),
  primaryContainer = Color(0xFFEDE9FE),
  onPrimaryContainer = Color(0xFF2E1065),
  secondary = Color(0xFF9333EA),
  secondaryContainer = Color(0xFFF3E8FF),
  onSecondaryContainer = Color(0xFF3B0764),
  tertiary = Color(0xFFBE185D),
  tertiaryContainer = Color(0xFFFCE7F3),
  onTertiaryContainer = Color(0xFF500724),
)

private val VioletDarkScheme = buildStudioDarkScheme(
  primary = Color(0xFFC4B5FD),
  onPrimary = Color(0xFF2E1065),
  primaryContainer = Color(0xFF5B21B6),
  onPrimaryContainer = Color(0xFFEDE9FE),
  secondary = Color(0xFFD8B4FE),
  onSecondary = Color(0xFF3B0764),
  secondaryContainer = Color(0xFF6B21A8),
  onSecondaryContainer = Color(0xFFF3E8FF),
  tertiary = Color(0xFFF472B6),
  onTertiary = Color(0xFF500724),
  tertiaryContainer = Color(0xFF831843),
  onTertiaryContainer = Color(0xFFFCE7F3),
)

val SymbolPadShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(16.dp),
  large = RoundedCornerShape(22.dp),
  extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun SymbolPadTheme(
  themeMode: AppThemeMode = AppThemeMode.SYSTEM,
  colorPalette: AppColorPalette = AppColorPalette.INDIGO,
  content: @Composable () -> Unit,
) {
  val isDarkTheme = when (themeMode) {
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
  }

  val colorScheme = when (colorPalette) {
    AppColorPalette.DYNAMIC -> {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      } else {
        if (isDarkTheme) IndigoDarkScheme else IndigoLightScheme
      }
    }
    AppColorPalette.INDIGO -> if (isDarkTheme) IndigoDarkScheme else IndigoLightScheme
    AppColorPalette.EMERALD -> if (isDarkTheme) EmeraldDarkScheme else EmeraldLightScheme
    AppColorPalette.ROSE -> if (isDarkTheme) RoseDarkScheme else RoseLightScheme
    AppColorPalette.AMBER -> if (isDarkTheme) AmberDarkScheme else AmberLightScheme
    AppColorPalette.OCEAN -> if (isDarkTheme) OceanDarkScheme else OceanLightScheme
    AppColorPalette.VIOLET -> if (isDarkTheme) VioletDarkScheme else VioletLightScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = SymbolPadTypography,
    shapes = SymbolPadShapes,
    content = content,
  )
}
