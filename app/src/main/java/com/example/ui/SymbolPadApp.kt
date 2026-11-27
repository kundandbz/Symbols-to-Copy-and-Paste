package com.example.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.components.CatalogDetailSheet
import com.example.ui.components.CompactBuildTrayBar
import com.example.ui.components.FloatingCopyBanner
import com.example.ui.components.ThemeCustomizationSheet
import com.example.ui.screens.EmojiScreen
import com.example.ui.screens.KaomojiScreen
import com.example.ui.screens.LennyScreen
import com.example.ui.screens.SymbolsScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VaultScreen

object SymbolPadRoutes {
  const val SYMBOLS = "symbols"
  const val EMOJIS = "emojis"
  const val KAOMOJI = "kaomoji"
  const val LENNY = "lenny"
  const val TOOLS = "tools"
  const val VAULT = "vault"
}

private data class NavDestinationItem(
  val route: String,
  val labelRes: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String,
)

private val primaryNavDestinations = listOf(
  NavDestinationItem(
    route = SymbolPadRoutes.SYMBOLS,
    labelRes = R.string.nav_symbols,
    selectedIcon = Icons.Filled.AutoAwesome,
    unselectedIcon = Icons.Outlined.AutoAwesome,
    testTag = "nav_symbols",
  ),
  NavDestinationItem(
    route = SymbolPadRoutes.EMOJIS,
    labelRes = R.string.nav_emojis,
    selectedIcon = Icons.Filled.EmojiEmotions,
    unselectedIcon = Icons.Outlined.EmojiEmotions,
    testTag = "nav_emojis",
  ),
  NavDestinationItem(
    route = SymbolPadRoutes.KAOMOJI,
    labelRes = R.string.nav_kaomoji,
    selectedIcon = Icons.Filled.SentimentVerySatisfied,
    unselectedIcon = Icons.Outlined.SentimentVerySatisfied,
    testTag = "nav_kaomoji",
  ),
  NavDestinationItem(
    route = SymbolPadRoutes.LENNY,
    labelRes = R.string.nav_lenny,
    selectedIcon = Icons.Filled.Face,
    unselectedIcon = Icons.Outlined.Face,
    testTag = "nav_lenny",
  ),
  NavDestinationItem(
    route = SymbolPadRoutes.TOOLS,
    labelRes = R.string.nav_tools,
    selectedIcon = Icons.Filled.Build,
    unselectedIcon = Icons.Outlined.Build,
    testTag = "nav_tools",
  ),
)

private val crossTabTrayRoutes = setOf(
  SymbolPadRoutes.EMOJIS,
  SymbolPadRoutes.KAOMOJI,
  SymbolPadRoutes.LENNY,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolPadApp(
  viewModel: SymbolPadViewModel,
) {
  val context = LocalContext.current
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination
  val currentRoute = currentDestination?.route
  val isVaultOpen = currentRoute == SymbolPadRoutes.VAULT

  var showThemeSheet by remember { mutableStateOf(false) }
  val websiteUrl = stringResource(R.string.website_url)

  val copyToClipboard = remember(context) {
    { text: String ->
      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
      clipboard.setPrimaryClip(ClipData.newPlainText("Symbolstocopyandpaste.com", text))
    }
  }

  val openWebVersion = remember(context, websiteUrl, viewModel, copyToClipboard) {
    {
      try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl))
        context.startActivity(intent)
      } catch (_: ActivityNotFoundException) {
        viewModel.copyText(websiteUrl, "Web Version Link", copyToClipboard)
      }
    }
  }

  val shareText = remember(context) {
    { text: String ->
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
      }
      context.startActivity(Intent.createChooser(intent, "Share symbol or text"))
    }
  }

  val navigateToPrimaryTab = remember(navController) {
    { route: String ->
      navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
          saveState = true
        }
        launchSingleTop = true
        restoreState = true
      }
    }
  }

  val openFavoritesVault = remember(viewModel, navController) {
    {
      viewModel.onVaultTabSelected(0)
      navController.navigate(SymbolPadRoutes.VAULT) { launchSingleTop = true }
    }
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useRail = maxWidth >= 600.dp

    Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,
      containerColor = MaterialTheme.colorScheme.background,
      topBar = {
        if (!isVaultOpen) {
          TopAppBar(
            title = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                Surface(
                  shape = MaterialTheme.shapes.small,
                  color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                  Text(
                    text = "✦",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  )
                }
                Column {
                  Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = stringResource(R.string.website_domain),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                }
              }
            },
            actions = {
              FilledTonalButton(
                onClick = openWebVersion,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                  .minimumInteractiveComponentSize()
                  .heightIn(min = 36.dp)
                  .testTag("open_web_version_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Language,
                  contentDescription = stringResource(R.string.web_version_open_desc),
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = stringResource(R.string.web_version_short),
                  style = MaterialTheme.typography.labelMedium,
                  maxLines = 1,
                )
              }
              IconButton(
                onClick = { showThemeSheet = true },
                modifier = Modifier.testTag("open_theme_sheet_button"),
              ) {
                Icon(
                  imageVector = Icons.Filled.Palette,
                  contentDescription = stringResource(R.string.theme_customize_title),
                )
              }
              TopBarFavoritesButton(
                viewModel = viewModel,
                onClick = openFavoritesVault,
              )
            },
            colors = TopAppBarDefaults.topAppBarColors(
              containerColor = MaterialTheme.colorScheme.background,
              titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
          )
        }
      },
      bottomBar = {
        if (!useRail && !isVaultOpen) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
          ) {
            primaryNavDestinations.forEach { dest ->
              val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
              val label = stringResource(dest.labelRes)
              NavigationBarItem(
                selected = selected,
                onClick = { navigateToPrimaryTab(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = {
                  Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (useRail && !isVaultOpen) {
          NavigationRail(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxHeight(),
          ) {
            primaryNavDestinations.forEach { dest ->
              val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
              val label = stringResource(dest.labelRes)
              NavigationRailItem(
                selected = selected,
                onClick = { navigateToPrimaryTab(dest.route) },
                icon = {
                  Icon(
                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                    contentDescription = label,
                  )
                },
                label = { Text(label) },
                modifier = Modifier.testTag(dest.testTag),
              )
            }
          }
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          contentAlignment = Alignment.TopCenter,
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .widthIn(max = 920.dp),
          ) {
            if (!isVaultOpen && currentRoute in crossTabTrayRoutes) {
              CrossTabBuildTrayHost(
                viewModel = viewModel,
                copyToClipboard = copyToClipboard,
              )
            }

            NavHost(
              navController = navController,
              startDestination = SymbolPadRoutes.SYMBOLS,
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            ) {
              composable(SymbolPadRoutes.SYMBOLS) {
                val symbolsState by viewModel.symbolsState.collectAsStateWithLifecycle()
                val composerState by viewModel.composerState.collectAsStateWithLifecycle()
                val favoriteGlyphs by viewModel.favoriteGlyphs.collectAsStateWithLifecycle()
                val quickFavorites by viewModel.mostUsedFavorites.collectAsStateWithLifecycle()

                SymbolsScreen(
                  state = symbolsState,
                  composerState = composerState,
                  favoriteGlyphs = favoriteGlyphs,
                  quickFavorites = quickFavorites,
                  onSearchQueryChange = viewModel::onSymbolsQueryChange,
                  onCategorySelected = viewModel::onSymbolsCategorySelected,
                  onToggleBuildMode = viewModel::toggleBuildMode,
                  onComposerTextChange = viewModel::onComposerTextChange,
                  onBackspaceComposer = viewModel::backspaceComposer,
                  onClearComposer = viewModel::clearComposer,
                  onSaveComposerSnippet = { content ->
                    viewModel.saveSnippet("Custom Combo", content, "Combo Tray")
                  },
                  onEntryTapped = { entry -> viewModel.onEntryTapped(entry, copyToClipboard) },
                  onEntryLongPressed = viewModel::openEntryDetail,
                  onToggleFavorite = viewModel::toggleFavorite,
                  onQuickFavoriteTapped = { fav -> viewModel.onFavoriteTapped(fav, copyToClipboard) },
                  onOpenFavorites = openFavoritesVault,
                  onCopyText = { text, label -> viewModel.copyText(text, label, copyToClipboard) },
                  onOpenWebVersion = openWebVersion,
                )
              }

              composable(SymbolPadRoutes.EMOJIS) {
                val emojiState by viewModel.emojiState.collectAsStateWithLifecycle()
                val favoriteGlyphs by viewModel.favoriteGlyphs.collectAsStateWithLifecycle()
                val composerState by viewModel.composerState.collectAsStateWithLifecycle()

                EmojiScreen(
                  state = emojiState,
                  favoriteGlyphs = favoriteGlyphs,
                  isBuildMode = composerState.isBuildMode,
                  onSearchQueryChange = viewModel::onEmojiQueryChange,
                  onCategorySelected = viewModel::onEmojiCategorySelected,
                  onEntryTapped = { entry -> viewModel.onEntryTapped(entry, copyToClipboard) },
                  onEntryLongPressed = viewModel::openEntryDetail,
                  onToggleFavorite = viewModel::toggleFavorite,
                )
              }

              composable(SymbolPadRoutes.KAOMOJI) {
                val kaomojiState by viewModel.kaomojiState.collectAsStateWithLifecycle()
                val favoriteGlyphs by viewModel.favoriteGlyphs.collectAsStateWithLifecycle()
                val composerState by viewModel.composerState.collectAsStateWithLifecycle()

                KaomojiScreen(
                  state = kaomojiState,
                  favoriteGlyphs = favoriteGlyphs,
                  isBuildMode = composerState.isBuildMode,
                  onSearchQueryChange = viewModel::onKaomojiQueryChange,
                  onCategorySelected = viewModel::onKaomojiCategorySelected,
                  onEntryTapped = { entry -> viewModel.onEntryTapped(entry, copyToClipboard) },
                  onEntryLongPressed = viewModel::openEntryDetail,
                  onToggleFavorite = viewModel::toggleFavorite,
                )
              }

              composable(SymbolPadRoutes.LENNY) {
                val lennyState by viewModel.lennyState.collectAsStateWithLifecycle()
                val lennyBuilder by viewModel.lennyBuilder.collectAsStateWithLifecycle()
                val favoriteGlyphs by viewModel.favoriteGlyphs.collectAsStateWithLifecycle()
                val composerState by viewModel.composerState.collectAsStateWithLifecycle()

                LennyScreen(
                  state = lennyState,
                  builderState = lennyBuilder,
                  favoriteGlyphs = favoriteGlyphs,
                  isBuildMode = composerState.isBuildMode,
                  onSearchQueryChange = viewModel::onLennyQueryChange,
                  onCategorySelected = viewModel::onLennyCategorySelected,
                  onSelectEar = viewModel::onSelectLennyEar,
                  onSelectEye = viewModel::onSelectLennyEye,
                  onSelectMouth = viewModel::onSelectLennyMouth,
                  onSelectAccessory = viewModel::onSelectLennyAccessory,
                  onRandomizeLenny = viewModel::randomizeLenny,
                  onCopyText = { text, label -> viewModel.copyText(text, label, copyToClipboard) },
                  onSaveSnippet = { title, content -> viewModel.saveSnippet(title, content, "Lenny Face") },
                  onEntryTapped = { entry -> viewModel.onEntryTapped(entry, copyToClipboard) },
                  onEntryLongPressed = viewModel::openEntryDetail,
                  onToggleFavorite = viewModel::toggleFavorite,
                )
              }

              composable(SymbolPadRoutes.TOOLS) {
                val toolsState by viewModel.toolsState.collectAsStateWithLifecycle()

                ToolsScreen(
                  state = toolsState,
                  onSelectToolTab = viewModel::onToolTabSelected,
                  onFontInputChange = viewModel::onFontInputChange,
                  onSelectFontFrame = viewModel::onSelectFontFrame,
                  onBioInputChange = viewModel::onBioInputChange,
                  onSpacerLineCountChange = viewModel::onSpacerLineCountChange,
                  onConverterInputChange = viewModel::onConverterInputChange,
                  onToggleMorseDecode = viewModel::onToggleMorseDecode,
                  onCopyText = { text, label -> viewModel.copyText(text, label, copyToClipboard) },
                  onSaveSnippet = viewModel::saveSnippet,
                  onShareText = shareText,
                )
              }

              composable(SymbolPadRoutes.VAULT) {
                val vaultState by viewModel.vaultState.collectAsStateWithLifecycle()

                VaultScreen(
                  state = vaultState,
                  onTabSelected = viewModel::onVaultTabSelected,
                  onFavoriteSortModeSelected = viewModel::onFavoriteSortModeSelected,
                  onFavoritesQueryChange = viewModel::onFavoritesQueryChange,
                  onCopyText = { text, label -> viewModel.copyText(text, label, copyToClipboard) },
                  onRemoveFavorite = viewModel::removeFavorite,
                  onClearHistory = viewModel::clearRecentHistory,
                  onSaveNewSnippet = { title, content -> viewModel.saveSnippet(title, content, "Custom") },
                  onDeleteSnippet = viewModel::deleteSnippet,
                  onShareText = shareText,
                  onNavigateToExplore = {
                    navController.popBackStack()
                    navigateToPrimaryTab(SymbolPadRoutes.SYMBOLS)
                  },
                  onBack = { navController.popBackStack() },
                )
              }
            }
          }

          FloatingCopyBannerHost(
            viewModel = viewModel,
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 16.dp),
          )
        }
      }
    }

    CatalogDetailSheetHost(
      viewModel = viewModel,
      copyToClipboard = copyToClipboard,
      shareText = shareText,
    )

    if (showThemeSheet) {
      val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()
      ThemeCustomizationSheet(
        settings = themeSettings,
        onSelectMode = viewModel::onThemeModeSelected,
        onSelectPalette = viewModel::onColorPaletteSelected,
        onOpenWebVersion = openWebVersion,
        onDismiss = { showThemeSheet = false },
      )
    }
  }
}

@Composable
private fun TopBarFavoritesButton(
  viewModel: SymbolPadViewModel,
  onClick: () -> Unit,
) {
  val favCount by viewModel.favoritesCount.collectAsStateWithLifecycle()
  IconButton(
    onClick = onClick,
    modifier = Modifier.testTag("nav_vault"),
  ) {
    BadgedBox(
      badge = {
        if (favCount > 0) {
          Badge { Text(if (favCount > 99) "99+" else favCount.toString()) }
        }
      },
    ) {
      Icon(
        imageVector = if (favCount > 0) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = stringResource(R.string.nav_favorites_desc),
        tint = if (favCount > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground,
      )
    }
  }
}

@Composable
private fun CrossTabBuildTrayHost(
  viewModel: SymbolPadViewModel,
  copyToClipboard: (String) -> Unit,
) {
  val composerState by viewModel.composerState.collectAsStateWithLifecycle()
  if (composerState.isBuildMode) {
    CompactBuildTrayBar(
      composerText = composerState.composerText,
      isBuildMode = composerState.isBuildMode,
      onBackspace = viewModel::backspaceComposer,
      onCopyAll = {
        if (composerState.composerText.isNotEmpty()) {
          viewModel.copyText(composerState.composerText, "Custom Combo", copyToClipboard)
        }
      },
      onExitBuildMode = { viewModel.toggleBuildMode(false) },
    )
  }
}

@Composable
private fun FloatingCopyBannerHost(
  viewModel: SymbolPadViewModel,
  modifier: Modifier = Modifier,
) {
  val copyBanner by viewModel.copyBanner.collectAsStateWithLifecycle()
  FloatingCopyBanner(
    banner = copyBanner,
    onDismiss = viewModel::dismissCopyBanner,
    modifier = modifier,
  )
}

@Composable
private fun CatalogDetailSheetHost(
  viewModel: SymbolPadViewModel,
  copyToClipboard: (String) -> Unit,
  shareText: (String) -> Unit,
) {
  val detailEntry by viewModel.detailEntry.collectAsStateWithLifecycle()
  detailEntry?.let { entry ->
    val favoriteGlyphs by viewModel.favoriteGlyphs.collectAsStateWithLifecycle()
    CatalogDetailSheet(
      item = entry,
      isFavorite = favoriteGlyphs.contains(entry.glyph),
      onDismiss = viewModel::dismissEntryDetail,
      onCopy = { viewModel.copyText(entry.glyph, entry.name, copyToClipboard) },
      onAppendToTray = { viewModel.appendEntryToTray(entry) },
      onToggleFavorite = { viewModel.toggleFavorite(entry) },
      onShare = { shareText(entry.glyph) },
    )
  }
}
