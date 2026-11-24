package com.example

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppThemeMode
import com.example.data.SymbolRepository
import com.example.data.ThemePreferencesRepository
import com.example.data.local.SymbolPadDatabase
import com.example.ui.SymbolPadApp
import com.example.ui.SymbolPadViewModel
import com.example.ui.theme.SymbolPadTheme

class MainActivity : ComponentActivity() {

  private val viewModel: SymbolPadViewModel by viewModels {
    val db = SymbolPadDatabase.getInstance(applicationContext)
    SymbolPadViewModel.Factory(
      repository = SymbolRepository(db.vaultDao()),
      themePreferences = ThemePreferencesRepository(applicationContext),
    )
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
      val isDark = when (themeSettings.mode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
      }

      DisposableEffect(isDark) {
        enableEdgeToEdge(
          statusBarStyle = if (isDark) {
            SystemBarStyle.dark(Color.TRANSPARENT)
          } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
          },
          navigationBarStyle = if (isDark) {
            SystemBarStyle.dark(Color.TRANSPARENT)
          } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
          },
        )
        onDispose {}
      }

      SymbolPadTheme(
        themeMode = themeSettings.mode,
        colorPalette = themeSettings.palette,
      ) {
        SymbolPadApp(viewModel = viewModel)
      }
    }
  }
}
