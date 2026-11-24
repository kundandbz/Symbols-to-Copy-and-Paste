package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppThemeMode(val label: String) {
  SYSTEM("System"),
  LIGHT("Light"),
  DARK("Dark"),
}

enum class AppColorPalette(
  val id: String,
  val label: String,
  val swatchHex: Long,
) {
  INDIGO("indigo", "Indigo", 0xFF3730A3),
  EMERALD("emerald", "Emerald", 0xFF047857),
  ROSE("rose", "Rose", 0xFFBE123C),
  AMBER("amber", "Amber", 0xFFB45309),
  OCEAN("ocean", "Ocean", 0xFF0369A1),
  VIOLET("violet", "Violet", 0xFF6D28D9),
  DYNAMIC("dynamic", "Wallpaper", 0xFF64748B),
}

data class ThemeSettings(
  val mode: AppThemeMode = AppThemeMode.SYSTEM,
  val palette: AppColorPalette = AppColorPalette.INDIGO,
)

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "symbol_pad_theme_prefs")

class ThemePreferencesRepository(
  private val context: Context,
) {
  private val keyMode = stringPreferencesKey("theme_mode")
  private val keyPalette = stringPreferencesKey("theme_palette")

  val themeSettings: Flow<ThemeSettings> = context.themeDataStore.data.map { prefs ->
    val mode = prefs[keyMode]?.let { saved ->
      AppThemeMode.entries.firstOrNull { it.name == saved }
    } ?: AppThemeMode.SYSTEM

    val palette = prefs[keyPalette]?.let { saved ->
      AppColorPalette.entries.firstOrNull { it.id == saved }
    } ?: AppColorPalette.INDIGO

    ThemeSettings(mode = mode, palette = palette)
  }

  suspend fun setThemeMode(mode: AppThemeMode) {
    context.themeDataStore.edit { prefs ->
      prefs[keyMode] = mode.name
    }
  }

  suspend fun setColorPalette(palette: AppColorPalette) {
    context.themeDataStore.edit { prefs ->
      prefs[keyPalette] = palette.id
    }
  }
}
