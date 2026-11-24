package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.FavoriteSymbolEntity
import com.example.model.CatalogCategory
import com.example.model.CatalogEntry
import com.example.model.SymbolCategories
import com.example.ui.SymbolsUiState
import com.example.ui.components.CatalogGlyphTile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolsScreen(
  state: SymbolsUiState,
  favoriteGlyphs: Set<String>,
  quickFavorites: List<FavoriteSymbolEntity>,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onToggleBuildMode: (Boolean) -> Unit,
  onComposerTextChange: (String) -> Unit,
  onBackspaceComposer: () -> Unit,
  onClearComposer: () -> Unit,
  onSaveComposerSnippet: (String) -> Unit,
  onEntryTapped: (CatalogEntry) -> Unit,
  onEntryLongPressed: (CatalogEntry) -> Unit,
  onToggleFavorite: (CatalogEntry) -> Unit,
  onQuickFavoriteTapped: (FavoriteSymbolEntity) -> Unit,
  onOpenFavorites: () -> Unit,
  onCopyText: (String, String) -> Unit,
  onOpenWebVersion: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val gridState = rememberLazyGridState()
  var showCategorySheet by remember { mutableStateOf(false) }
  var lastCategoryId by remember { mutableStateOf(state.selectedCategoryId) }

  LaunchedEffect(state.selectedCategoryId) {
    if (lastCategoryId != state.selectedCategoryId) {
      lastCategoryId = state.selectedCategoryId
      gridState.scrollToItem(0)
    }
  }

  val selectedCategory = remember(state.selectedCategoryId) {
    SymbolCategories.all.firstOrNull { it.id == state.selectedCategoryId } ?: SymbolCategories.ALL
  }

  val realCategoryCount = remember {
    SymbolCategories.all.count { it.id != "all" && it.id != "favorites" }
  }

  val groupedByCategory = remember(state.filteredSymbols, state.selectedCategoryId, state.searchQuery) {
    if (state.selectedCategoryId == "all" && state.searchQuery.isBlank()) {
      SymbolCategories.all
        .filter { it.id != "all" && it.id != "favorites" }
        .mapNotNull { cat ->
          val items = state.filteredSymbols.filter { it.categoryId == cat.id }
          if (items.isEmpty()) null else cat to items
        }
    } else {
      null
    }
  }

  if (showCategorySheet) {
    CategoryDirectorySheet(
      selectedCategoryId = state.selectedCategoryId,
      categoryCounts = state.categoryCounts,
      favoriteSymbolCount = state.favoriteSymbolCount,
      totalSymbolCount = state.totalSymbolCount,
      onSelectCategory = { catId ->
        onCategorySelected(catId)
        showCategorySheet = false
      },
      onDismiss = { showCategorySheet = false },
    )
  }

  LazyVerticalGrid(
    state = gridState,
    columns = GridCells.Adaptive(minSize = 88.dp),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    // Studio Hero Banner
    item(span = { GridItemSpan(maxLineSpan) }) {
      SymbolsHeroHeader(
        totalSymbolCount = state.totalSymbolCount,
        categoryCount = realCategoryCount,
        isBuildMode = state.isBuildMode,
        onToggleBuildMode = onToggleBuildMode,
        onOpenWebVersion = onOpenWebVersion,
      )
    }

    // Search Bar + Build Mode Tray
    item(span = { GridItemSpan(maxLineSpan) }) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = state.searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = {
            Text(
              text = stringResource(R.string.search_symbols_hint),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Filled.Search,
              contentDescription = "Search symbols",
              tint = MaterialTheme.colorScheme.primary,
            )
          },
          trailingIcon = {
            if (state.searchQuery.isNotEmpty()) {
              IconButton(
                onClick = { onSearchQueryChange("") },
                modifier = Modifier.testTag("clear_search_button"),
              ) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear search")
              }
            }
          },
          singleLine = true,
          shape = MaterialTheme.shapes.large,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("symbol_search_input"),
        )

        AnimatedVisibility(visible = state.isBuildMode || state.composerText.isNotEmpty()) {
          ComposerTrayCard(
            text = state.composerText,
            onTextChange = onComposerTextChange,
            onAddSpace = { onComposerTextChange(state.composerText + " ") },
            onBackspace = onBackspaceComposer,
            onClear = onClearComposer,
            onSave = {
              if (state.composerText.isNotEmpty()) {
                onSaveComposerSnippet(state.composerText)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              }
            },
            onCopyAll = {
              if (state.composerText.isNotEmpty()) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(state.composerText, "Custom Combo")
              }
            },
          )
        }
      }
    }

    // Pinned Room-Backed Quick Favorites Strip (sorted by Most Used)
    if (state.searchQuery.isBlank() && state.selectedCategoryId == "all" && quickFavorites.isNotEmpty()) {
      item(key = "quick_favorites_strip", span = { GridItemSpan(maxLineSpan) }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(16.dp),
              )
              Text(
                text = "${stringResource(R.string.favorites_quick_title)} (${quickFavorites.size})",
                style = MaterialTheme.typography.titleSmall,
              )
            }
            TextButton(
              onClick = onOpenFavorites,
              modifier = Modifier.testTag("manage_favorites_button"),
            ) {
              Text(stringResource(R.string.favorites_manage_all))
            }
          }
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("quick_favorites_row"),
          ) {
            items(quickFavorites.take(20), key = { "qfav_${it.glyph}" }) { fav ->
              QuickFavoriteChip(
                favorite = fav,
                isBuildMode = state.isBuildMode,
                onTap = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  onQuickFavoriteTapped(fav)
                },
              )
            }
          }
        }
      }
    }

    // Trending Quick-Copy Strip
    if (state.searchQuery.isBlank() && state.selectedCategoryId == "all") {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = "Popular Copy & Paste",
              style = MaterialTheme.typography.titleSmall,
            )
            Text(
              text = if (state.isBuildMode) "Tap to append" else "1-tap copy · Hold for info",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.trendingSymbols, key = { "trend_${it.id}" }) { item ->
              TrendingSymbolChip(
                item = item,
                isBuildMode = state.isBuildMode,
                onTap = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  onEntryTapped(item)
                },
              )
            }
          }
        }
      }
    }

    // Category Directory Button + Horizontal Category Chips
    item(span = { GridItemSpan(maxLineSpan) }) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Symbol Categories",
            style = MaterialTheme.typography.titleSmall,
          )
          FilledTonalButton(
            onClick = { showCategorySheet = true },
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            modifier = Modifier
              .heightIn(min = 48.dp)
              .testTag("open_categories_directory"),
          ) {
            Icon(Icons.Filled.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("All $realCategoryCount Categories", style = MaterialTheme.typography.labelMedium)
          }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(SymbolCategories.all, key = { it.id }) { category ->
            val selected = state.selectedCategoryId == category.id
            FilterChip(
              selected = selected,
              onClick = { onCategorySelected(category.id) },
              label = {
                Text(
                  text = "${category.badge}  ${category.label}",
                  style = MaterialTheme.typography.labelMedium,
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
              modifier = Modifier.testTag("symbol_cat_${category.id}"),
            )
          }
        }
      }
    }

    // Either categorized sections (matching website layout!) or single filtered list
    if (state.filteredSymbols.isEmpty()) {
      item(span = { GridItemSpan(maxLineSpan) }) {
        if (state.selectedCategoryId == "favorites" && state.searchQuery.isBlank()) {
          EmptyFavoritesFilterCard(
            onBrowseAll = { onCategorySelected("all") },
          )
        } else {
          EmptySearchCard(
            onReset = {
              onSearchQueryChange("")
              onCategorySelected("all")
            },
          )
        }
      }
    } else if (groupedByCategory != null) {
      groupedByCategory.forEach { (category, itemsInCategory) ->
        item(
          key = "header_${category.id}",
          span = { GridItemSpan(maxLineSpan) },
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
              ) {
                Text(
                  text = category.badge,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
              Text(
                text = "${category.label} (${itemsInCategory.size})",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            TextButton(onClick = { onCategorySelected(category.id) }) {
              Text("Focus")
            }
          }
        }

        items(
          items = itemsInCategory,
          key = { it.id },
          span = { item -> if (item.isWide) GridItemSpan(maxLineSpan) else GridItemSpan(1) },
        ) { item ->
          CatalogGlyphTile(
            item = item,
            isFavorite = favoriteGlyphs.contains(item.glyph),
            isBuildMode = state.isBuildMode,
            onTap = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onEntryTapped(item)
            },
            onLongPress = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onEntryLongPressed(item)
            },
            onToggleFavorite = { onToggleFavorite(item) },
          )
        }
      }
    } else {
      item(span = { GridItemSpan(maxLineSpan) }) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "${selectedCategory.label} (${state.filteredSymbols.size})",
            style = MaterialTheme.typography.titleMedium,
          )
          TextButton(
            onClick = {
              onSearchQueryChange("")
              onCategorySelected("all")
            },
          ) {
            Text("Show All")
          }
        }
      }

      items(
        items = state.filteredSymbols,
        key = { it.id },
        span = { item -> if (item.isWide) GridItemSpan(maxLineSpan) else GridItemSpan(1) },
      ) { item ->
        CatalogGlyphTile(
          item = item,
          isFavorite = favoriteGlyphs.contains(item.glyph),
          isBuildMode = state.isBuildMode,
          onTap = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onEntryTapped(item)
          },
          onLongPress = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onEntryLongPressed(item)
          },
          onToggleFavorite = { onToggleFavorite(item) },
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDirectorySheet(
  selectedCategoryId: String,
  categoryCounts: Map<String, Int>,
  favoriteSymbolCount: Int,
  totalSymbolCount: Int,
  onSelectCategory: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = "All Symbol Categories",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(horizontal = 4.dp),
      )
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 152.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(420.dp),
      ) {
        items(SymbolCategories.all, key = { "dir_${it.id}" }) { cat ->
          val selected = cat.id == selectedCategoryId
          val count = when (cat.id) {
            "all" -> totalSymbolCount
            "favorites" -> favoriteSymbolCount
            else -> categoryCounts[cat.id] ?: 0
          }
          Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow,
            border = BorderStroke(
              1.dp,
              if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            ),
            modifier = Modifier
              .clip(MaterialTheme.shapes.medium)
              .clickable { onSelectCategory(cat.id) },
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Text(text = cat.badge, fontSize = 18.sp)
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = cat.label,
                  style = MaterialTheme.typography.labelLarge,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "$count symbols",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SymbolsHeroHeader(
  totalSymbolCount: Int,
  categoryCount: Int,
  isBuildMode: Boolean,
  onToggleBuildMode: (Boolean) -> Unit,
  onOpenWebVersion: () -> Unit,
) {
  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    tonalElevation = 2.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(148.dp),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_studio_banner),
        contentDescription = "Symbols decorative banner",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color(0xE61E1B4B),
                Color(0xBF1E1B4B),
                Color(0x731E1B4B),
              )
            )
          ),
      )
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = Color(0x33FBBF24),
              modifier = Modifier.weight(1f, fill = false),
            ) {
              Text(
                text = "✦ $totalSymbolCount SYMBOLS · $categoryCount CATS",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFDE68A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
            Spacer(modifier = Modifier.weight(0.01f))
            Surface(
              onClick = onOpenWebVersion,
              shape = RoundedCornerShape(999.dp),
              color = Color(0xEBFFFFFF),
              modifier = Modifier.testTag("hero_web_version_button"),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Language,
                  contentDescription = stringResource(R.string.web_version_open_desc),
                  tint = Color(0xFF1E1B4B),
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = stringResource(R.string.web_version_label),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1E1B4B),
                  maxLines = 1,
                )
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                  contentDescription = null,
                  tint = Color(0xFF1E1B4B),
                  modifier = Modifier.size(12.dp),
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Symbols to Copy & Paste",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = if (isBuildMode) Icons.Filled.EditNote else Icons.Filled.TouchApp,
              contentDescription = null,
              tint = Color(0xFFFBBF24),
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = if (isBuildMode) "Build Mode: Tap to combine" else "Instant Mode: 1-tap copy",
              style = MaterialTheme.typography.labelMedium,
              color = Color(0xFFE0E7FF),
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Tray",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Switch(
              checked = isBuildMode,
              onCheckedChange = onToggleBuildMode,
              modifier = Modifier.testTag("build_mode_switch"),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ComposerTrayCard(
  text: String,
  onTextChange: (String) -> Unit,
  onAddSpace: () -> Unit,
  onBackspace: () -> Unit,
  onClear: () -> Unit,
  onSave: () -> Unit,
  onCopyAll: () -> Unit,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = "Combo Builder Tray",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        if (text.isNotEmpty()) {
          Text(
            text = "${text.length} chars",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text(stringResource(R.string.composer_placeholder)) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("composer_tray_input"),
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onAddSpace,
            modifier = Modifier
              .size(48.dp)
              .testTag("composer_space_button"),
          ) {
            Icon(Icons.Filled.SpaceBar, contentDescription = "Insert space")
          }
          IconButton(
            onClick = onBackspace,
            enabled = text.isNotEmpty(),
            modifier = Modifier
              .size(48.dp)
              .testTag("composer_backspace_button"),
          ) {
            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
          }
          IconButton(
            onClick = onClear,
            enabled = text.isNotEmpty(),
            modifier = Modifier
              .size(48.dp)
              .testTag("composer_clear_button"),
          ) {
            Icon(Icons.Filled.Clear, contentDescription = "Clear tray")
          }
          IconButton(
            onClick = onSave,
            enabled = text.isNotEmpty(),
            modifier = Modifier
              .size(48.dp)
              .testTag("composer_save_button"),
          ) {
            Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save to Vault")
          }
        }

        Button(
          onClick = onCopyAll,
          enabled = text.isNotEmpty(),
          modifier = Modifier
            .height(48.dp)
            .testTag("composer_copy_all_button"),
        ) {
          Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy All")
        }
      }
    }
  }
}

@Composable
private fun TrendingSymbolChip(
  item: CatalogEntry,
  isBuildMode: Boolean,
  onTap: () -> Unit,
) {
  var tapPulse by remember(item.id) { mutableIntStateOf(0) }
  var showCheck by remember(item.id) { mutableStateOf(false) }
  val chipScale = remember(item.id) { Animatable(1f) }

  LaunchedEffect(tapPulse) {
    if (tapPulse > 0) {
      showCheck = true
      launch {
        chipScale.animateTo(0.92f, animationSpec = tween(70))
        chipScale.animateTo(
          targetValue = 1f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        )
      }
      delay(900)
      showCheck = false
    }
  }

  val bgColor by animateColorAsState(
    targetValue = if (showCheck) {
      if (isBuildMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    } else {
      MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    },
    animationSpec = tween(180),
    label = "trending_chip_bg",
  )
  val contentColor by animateColorAsState(
    targetValue = if (showCheck) {
      if (isBuildMode) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    } else {
      MaterialTheme.colorScheme.onPrimaryContainer
    },
    animationSpec = tween(180),
    label = "trending_chip_content",
  )

  Surface(
    shape = RoundedCornerShape(999.dp),
    color = bgColor,
    border = BorderStroke(
      width = 1.dp,
      color = if (showCheck) bgColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
    ),
    modifier = Modifier
      .graphicsLayer {
        scaleX = chipScale.value
        scaleY = chipScale.value
      }
      .clip(RoundedCornerShape(999.dp))
      .clickable {
        tapPulse++
        onTap()
      }
      .testTag("trending_chip_${item.id}"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      AnimatedVisibility(
        visible = showCheck,
        enter = scaleIn(
          initialScale = 0.3f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        ) + fadeIn(tween(120)),
        exit = scaleOut(targetScale = 0.4f, animationSpec = tween(120)) + fadeOut(tween(120)),
      ) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = if (isBuildMode) "Added" else "Copied",
          tint = contentColor,
          modifier = Modifier.size(15.dp),
        )
      }
      Text(
        text = item.glyph,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = contentColor,
      )
    }
  }
}

@Composable
private fun QuickFavoriteChip(
  favorite: FavoriteSymbolEntity,
  isBuildMode: Boolean,
  onTap: () -> Unit,
) {
  var tapPulse by remember(favorite.glyph) { mutableIntStateOf(0) }
  var showCheck by remember(favorite.glyph) { mutableStateOf(false) }
  val chipScale = remember(favorite.glyph) { Animatable(1f) }

  LaunchedEffect(tapPulse) {
    if (tapPulse > 0) {
      showCheck = true
      launch {
        chipScale.animateTo(0.92f, animationSpec = tween(70))
        chipScale.animateTo(
          targetValue = 1f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        )
      }
      delay(900)
      showCheck = false
    }
  }

  val bgColor by animateColorAsState(
    targetValue = if (showCheck) {
      if (isBuildMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
    } else {
      MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
    },
    animationSpec = tween(180),
    label = "quick_fav_bg",
  )
  val contentColor by animateColorAsState(
    targetValue = if (showCheck) {
      if (isBuildMode) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
    } else {
      MaterialTheme.colorScheme.onTertiaryContainer
    },
    animationSpec = tween(180),
    label = "quick_fav_content",
  )

  Surface(
    shape = RoundedCornerShape(999.dp),
    color = bgColor,
    border = BorderStroke(
      width = 1.dp,
      color = if (showCheck) bgColor else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
    ),
    modifier = Modifier
      .graphicsLayer {
        scaleX = chipScale.value
        scaleY = chipScale.value
      }
      .clip(RoundedCornerShape(999.dp))
      .clickable {
        tapPulse++
        onTap()
      }
      .testTag("quick_fav_chip_${favorite.glyph.hashCode()}"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      androidx.compose.animation.AnimatedVisibility(
        visible = showCheck,
        enter = scaleIn(
          initialScale = 0.3f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        ) + fadeIn(tween(120)),
        exit = scaleOut(targetScale = 0.4f, animationSpec = tween(120)) + fadeOut(tween(120)),
      ) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = if (isBuildMode) "Added" else "Copied",
          tint = contentColor,
          modifier = Modifier.size(15.dp),
        )
      }
      Text(
        text = favorite.glyph,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      if (favorite.useCount > 0 && !showCheck) {
        Text(
          text = "${favorite.useCount}×",
          style = MaterialTheme.typography.labelSmall,
          color = contentColor.copy(alpha = 0.72f),
        )
      }
    }
  }
}

@Composable
fun EmptyFavoritesFilterCard(
  onBrowseAll: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp)
      .testTag("empty_favorites_filter_card"),
  ) {
    Column(
      modifier = Modifier.padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        imageVector = Icons.Outlined.FavoriteBorder,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.size(36.dp),
      )
      Text(
        text = stringResource(R.string.favorites_empty_filter_title),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
      )
      Text(
        text = stringResource(R.string.favorites_empty_filter_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
      FilledTonalButton(
        onClick = onBrowseAll,
        modifier = Modifier.heightIn(min = 48.dp),
      ) {
        Text("Browse All")
      }
    }
  }
}

@Composable
fun EmptySearchCard(
  onReset: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
  ) {
    Column(
      modifier = Modifier.padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text(text = "¯\\_(ツ)_/¯", fontSize = 30.sp)
      Text(
        text = stringResource(R.string.empty_search_title),
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = stringResource(R.string.empty_search_subtitle),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      TextButton(onClick = onReset) {
        Text("Reset Filters")
      }
    }
  }
}
