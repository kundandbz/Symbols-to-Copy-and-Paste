package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LennyBuilderParts
import com.example.model.CatalogEntry
import com.example.model.LennyCategories
import com.example.ui.LennyBuilderUiState
import com.example.ui.SectionBrowseUiState
import com.example.ui.components.CatalogGlyphTile

@Composable
fun LennyScreen(
  state: SectionBrowseUiState,
  builderState: LennyBuilderUiState,
  favoriteGlyphs: Set<String>,
  isBuildMode: Boolean,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onSelectEar: (Int) -> Unit,
  onSelectEye: (Int) -> Unit,
  onSelectMouth: (Int) -> Unit,
  onSelectAccessory: (Int) -> Unit,
  onRandomizeLenny: () -> Unit,
  onCopyText: (String, String) -> Unit,
  onSaveSnippet: (String, String) -> Unit,
  onEntryTapped: (CatalogEntry) -> Unit,
  onEntryLongPressed: (CatalogEntry) -> Unit,
  onToggleFavorite: (CatalogEntry) -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val listState = rememberLazyListState()
  var showCustomizer by remember { mutableStateOf(false) }
  var lastCategoryId by remember { mutableStateOf(state.selectedCategoryId) }

  LaunchedEffect(state.selectedCategoryId) {
    if (lastCategoryId != state.selectedCategoryId) {
      lastCategoryId = state.selectedCategoryId
      listState.scrollToItem(0)
    }
  }

  val grouped = state.groupedSections

  LazyColumn(
    state = listState,
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "Lenny Faces ( ͡° ͜ʖ ͡°)",
          style = MaterialTheme.typography.headlineSmall,
        )
        Text(
          text = "Browse ${state.totalCount} classic Lenny faces or mix ears, eyes, and mouths below.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    // Interactive Lenny Face Builder Card
    item {
      Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
              )
              Text(
                text = "Lenny Face Generator",
                style = MaterialTheme.typography.titleMedium,
              )
            }
            TextButton(
              onClick = { showCustomizer = !showCustomizer },
              modifier = Modifier.testTag("toggle_lenny_customizer"),
            ) {
              Text(if (showCustomizer) "Hide Parts" else "Customize")
              Icon(
                imageVector = if (showCustomizer) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
              )
            }
          }

          Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = builderState.builtFace,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("lenny_builder_preview"),
              )
            }
          }

          AnimatedVisibility(visible = showCustomizer) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              BuilderPartRow(
                label = "Ears & Arms",
                items = LennyBuilderParts.ears.map { "${it.left.trim()}·${it.right.trim()}" },
                selectedIndex = builderState.earIndex,
                onSelect = onSelectEar,
              )
              BuilderPartRow(
                label = "Eyes",
                items = LennyBuilderParts.eyes.map { "${it.first} ${it.second}" },
                selectedIndex = builderState.eyeIndex,
                onSelect = onSelectEye,
              )
              BuilderPartRow(
                label = "Mouth",
                items = LennyBuilderParts.mouths.map { it.trim().ifEmpty { "_" } },
                selectedIndex = builderState.mouthIndex,
                onSelect = onSelectMouth,
              )
              BuilderPartRow(
                label = "Extras",
                items = LennyBuilderParts.accessories.mapIndexed { idx, pair ->
                  if (idx == 0) "None" else "${pair.first}${pair.second}".trim()
                },
                selectedIndex = builderState.accessoryIndex,
                onSelect = onSelectAccessory,
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              FilledTonalIconButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  onRandomizeLenny()
                },
                modifier = Modifier
                  .size(48.dp)
                  .testTag("lenny_randomize_button"),
              ) {
                Icon(Icons.Filled.Casino, contentDescription = "Randomize Lenny Face")
              }
              FilledTonalIconButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                  onSaveSnippet("Custom Lenny", builderState.builtFace)
                },
                modifier = Modifier.size(48.dp),
              ) {
                Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save custom Lenny")
              }
            }

            Button(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(builderState.builtFace, "Custom Lenny Face")
              },
              modifier = Modifier
                .height(48.dp)
                .testTag("lenny_copy_built_button"),
            ) {
              Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Copy Face")
            }
          }
        }
      }
    }

    item {
      OutlinedTextField(
        value = state.searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
          Text(
            text = stringResource(R.string.search_lenny_hint),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search Lenny faces",
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
          .testTag("lenny_search_input"),
      )
    }

    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(LennyCategories.all, key = { it.id }) { category ->
          val selected = state.selectedCategoryId == category.id
          FilterChip(
            selected = selected,
            onClick = { onCategorySelected(category.id) },
            label = { Text("${category.badge}  ${category.label}", style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier.testTag("lenny_cat_${category.id}"),
          )
        }
      }
    }

    if (state.items.isEmpty()) {
      item {
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
        item(key = "ln_hdr_${category.id}", contentType = "section_header") {
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
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
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
        items(items = list, key = { it.id }, contentType = { "lenny_tile" }) { entry ->
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
      items(items = state.items, key = { it.id }, contentType = { "lenny_tile" }) { entry ->
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

@Composable
private fun BuilderPartRow(
  label: String,
  items: List<String>,
  selectedIndex: Int,
  onSelect: (Int) -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      itemsIndexed(items) { index, text ->
        FilterChip(
          selected = selectedIndex == index,
          onClick = { onSelect(index) },
          label = { Text(text, fontSize = 14.sp) },
        )
      }
    }
  }
}
