package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CatalogEntry
import com.example.model.EmojiCategories
import com.example.ui.SectionBrowseUiState
import com.example.ui.components.CatalogGlyphTile

@Composable
fun EmojiScreen(
  state: SectionBrowseUiState,
  favoriteGlyphs: Set<String>,
  isBuildMode: Boolean,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onEntryTapped: (CatalogEntry) -> Unit,
  onEntryLongPressed: (CatalogEntry) -> Unit,
  onToggleFavorite: (CatalogEntry) -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val gridState = rememberLazyGridState()
  var lastCategoryId by remember { mutableStateOf(state.selectedCategoryId) }

  LaunchedEffect(state.selectedCategoryId) {
    if (lastCategoryId != state.selectedCategoryId) {
      lastCategoryId = state.selectedCategoryId
      gridState.scrollToItem(0)
    }
  }

  val grouped = state.groupedSections

  LazyVerticalGrid(
    state = gridState,
    columns = GridCells.Adaptive(minSize = 84.dp),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item(span = { GridItemSpan(maxLineSpan) }) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "Emojis to Copy & Paste",
          style = MaterialTheme.typography.headlineSmall,
        )
        Text(
          text = "${state.totalCount} categorized emojis — tap any emoji to copy immediately.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    item(span = { GridItemSpan(maxLineSpan) }) {
      OutlinedTextField(
        value = state.searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
          Text(
            text = stringResource(R.string.search_emojis_hint),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search emojis",
            tint = MaterialTheme.colorScheme.primary,
          )
        },
        trailingIcon = {
          if (state.searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Filled.Clear, contentDescription = "Clear search")
            }
          }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("emoji_search_input"),
      )
    }

    item(span = { GridItemSpan(maxLineSpan) }) {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(EmojiCategories.all, key = { it.id }) { category ->
          val selected = state.selectedCategoryId == category.id
          FilterChip(
            selected = selected,
            onClick = { onCategorySelected(category.id) },
            label = { Text("${category.badge}  ${category.label}", style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier.testTag("emoji_cat_${category.id}"),
          )
        }
      }
    }

    if (state.items.isEmpty()) {
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
    } else if (grouped != null) {
      grouped.forEach { (category, list) ->
        item(key = "em_hdr_${category.id}", contentType = "section_header", span = { GridItemSpan(maxLineSpan) }) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
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
                  fontSize = 15.sp,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
              }
              Text(
                text = "${category.label} (${list.size})",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            TextButton(onClick = { onCategorySelected(category.id) }) {
              Text("Focus")
            }
          }
        }
        items(items = list, key = { it.id }, contentType = { "emoji_tile" }) { entry ->
          CatalogGlyphTile(
            item = entry,
            isFavorite = favoriteGlyphs.contains(entry.glyph),
            isBuildMode = isBuildMode,
            onTap = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onEntryTapped(entry)
            },
            onLongPress = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onEntryLongPressed(entry)
            },
            onToggleFavorite = { onToggleFavorite(entry) },
          )
        }
      }
    } else {
      items(items = state.items, key = { it.id }, contentType = { "emoji_tile" }) { entry ->
        CatalogGlyphTile(
          item = entry,
          isFavorite = favoriteGlyphs.contains(entry.glyph),
          isBuildMode = isBuildMode,
          onTap = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onEntryTapped(entry)
          },
          onLongPress = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onEntryLongPressed(entry)
          },
          onToggleFavorite = { onToggleFavorite(entry) },
        )
      }
    }
  }
}
