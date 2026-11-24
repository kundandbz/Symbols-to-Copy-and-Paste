package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.model.UnicodeTextUtils
import com.example.ui.FavoriteSortMode
import com.example.ui.VaultUiState
import java.text.DateFormat
import java.util.Date

@Composable
fun VaultScreen(
  state: VaultUiState,
  onTabSelected: (Int) -> Unit,
  onFavoriteSortModeSelected: (FavoriteSortMode) -> Unit,
  onFavoritesQueryChange: (String) -> Unit,
  onCopyText: (String, String) -> Unit,
  onRemoveFavorite: (String) -> Unit,
  onClearHistory: () -> Unit,
  onSaveNewSnippet: (String, String) -> Unit,
  onDeleteSnippet: (Long) -> Unit,
  onShareText: (String) -> Unit,
  onNavigateToExplore: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  var showNewSnippetDialog by remember { mutableStateOf(false) }

  if (showNewSnippetDialog) {
    NewSnippetDialog(
      onDismiss = { showNewSnippetDialog = false },
      onSave = { title, content ->
        onSaveNewSnippet(title, content)
        showNewSnippetDialog = false
      },
    )
  }

  LazyColumn(
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onBack,
              modifier = Modifier
                .size(48.dp)
                .testTag("vault_back_button"),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
              )
            }
            Text(
              text = stringResource(R.string.nav_favorites_desc),
              style = MaterialTheme.typography.headlineSmall,
            )
          }
          if (state.activeTab == 2) {
            FilledTonalButton(
              onClick = { showNewSnippetDialog = true },
              modifier = Modifier
                .height(48.dp)
                .testTag("add_snippet_button"),
            ) {
              Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New")
            }
          } else if (state.activeTab == 1 && state.recentCopies.isNotEmpty()) {
            TextButton(
              onClick = onClearHistory,
              modifier = Modifier.testTag("clear_history_button"),
            ) {
              Icon(Icons.Outlined.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear")
            }
          }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
          val tabs = listOf(
            "Favorites (${state.totalFavoritesCount})",
            "Recent (${state.recentCopies.size})",
            "Snippets (${state.savedSnippets.size})",
          )
          tabs.forEachIndexed { index, label ->
            SegmentedButton(
              selected = state.activeTab == index,
              onClick = { onTabSelected(index) },
              shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
              modifier = Modifier.testTag("vault_tab_$index"),
            ) {
              Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
          }
        }
      }
    }

    when (state.activeTab) {
      0 -> {
        if (state.totalFavoritesCount == 0) {
          item {
            EmptyVaultCard(
              title = stringResource(R.string.empty_vault_favorites_title),
              subtitle = stringResource(R.string.empty_vault_favorites_subtitle),
              ctaLabel = "Browse Symbols",
              onCtaClick = onNavigateToExplore,
            )
          }
        } else {
          item(key = "favorites_controls") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                FavoriteSortMode.entries.forEach { mode ->
                  val selected = state.favoriteSortMode == mode
                  FilterChip(
                    selected = selected,
                    onClick = { onFavoriteSortModeSelected(mode) },
                    leadingIcon = {
                      Icon(
                        imageVector = if (mode == FavoriteSortMode.MOST_USED) {
                          Icons.AutoMirrored.Filled.TrendingUp
                        } else {
                          Icons.Filled.Schedule
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                      )
                    },
                    label = { Text(mode.label, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                      selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                      selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    ),
                    modifier = Modifier.testTag("fav_sort_${mode.name.lowercase()}"),
                  )
                }
              }

              if (state.totalFavoritesCount >= 4 || state.favoritesQuery.isNotEmpty()) {
                OutlinedTextField(
                  value = state.favoritesQuery,
                  onValueChange = onFavoritesQueryChange,
                  placeholder = {
                    Text(
                      text = stringResource(R.string.favorites_search_hint),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Filled.Search,
                      contentDescription = "Search saved favorites",
                      tint = MaterialTheme.colorScheme.tertiary,
                    )
                  },
                  trailingIcon = {
                    if (state.favoritesQuery.isNotEmpty()) {
                      IconButton(onClick = { onFavoritesQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear favorites search")
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
                    .testTag("favorites_search_input"),
                )
              }
            }
          }

          if (state.favorites.isEmpty()) {
            item(key = "favorites_search_empty") {
              EmptySearchCard(
                onReset = { onFavoritesQueryChange("") },
              )
            }
          } else {
            items(state.favorites, key = { it.glyph }) { fav ->
              VaultItemCard(
                primaryText = fav.glyph,
                title = fav.label,
                badge = fav.categoryName,
                usageBadge = if (fav.useCount > 0) "Used ${fav.useCount}×" else null,
                onCopy = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  onCopyText(fav.glyph, fav.label)
                },
                onShare = { onShareText(fav.glyph) },
                trailingAction = {
                  IconButton(
                    onClick = { onRemoveFavorite(fav.glyph) },
                    modifier = Modifier
                      .size(48.dp)
                      .testTag("vault_remove_fav_${fav.glyph.hashCode()}"),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Favorite,
                      contentDescription = "Remove favorite",
                      tint = MaterialTheme.colorScheme.tertiary,
                    )
                  }
                },
              )
            }
          }
        }
      }

      1 -> {
        if (state.recentCopies.isEmpty()) {
          item {
            EmptyVaultCard(
              title = stringResource(R.string.empty_vault_recent_title),
              subtitle = stringResource(R.string.empty_vault_recent_subtitle),
              ctaLabel = "Explore Symbols",
              onCtaClick = onNavigateToExplore,
            )
          }
        } else {
          items(state.recentCopies, key = { it.id }) { recent ->
            val timeLabel = remember(recent.copiedAt) {
              DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(recent.copiedAt))
            }
            VaultItemCard(
              primaryText = recent.text,
              title = recent.sourceLabel,
              badge = timeLabel,
              onCopy = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(recent.text, recent.sourceLabel)
              },
              onShare = { onShareText(recent.text) },
            )
          }
        }
      }

      else -> {
        if (state.savedSnippets.isEmpty()) {
          item {
            EmptyVaultCard(
              title = "No custom snippets saved",
              subtitle = "Build a custom combo in the Tray, style a font, or tap New to save your own bio snippets.",
              ctaLabel = "Create Snippet",
              onCtaClick = { showNewSnippetDialog = true },
            )
          }
        } else {
          items(state.savedSnippets, key = { it.id }) { snippet ->
            VaultItemCard(
              primaryText = snippet.content,
              title = snippet.title,
              badge = snippet.kind,
              onCopy = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(snippet.content, snippet.title)
              },
              onShare = { onShareText(snippet.content) },
              trailingAction = {
                IconButton(
                  onClick = { onDeleteSnippet(snippet.id) },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Delete snippet",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              },
            )
          }
        }
      }
    }
  }
}

@Composable
private fun VaultItemCard(
  primaryText: String,
  title: String,
  badge: String,
  onCopy: () -> Unit,
  onShare: () -> Unit,
  usageBadge: String? = null,
  trailingAction: (@Composable () -> Unit)? = null,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.large)
      .clickable(onClick = onCopy),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      val isInvisible = remember(primaryText) { UnicodeTextUtils.isInvisibleText(primaryText) }
      val displayPrimary = remember(primaryText, title, isInvisible) {
        if (isInvisible) UnicodeTextUtils.formatVisiblePreview(primaryText, title) else primaryText
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = displayPrimary,
          fontSize = if (isInvisible) 15.sp else 19.sp,
          fontWeight = FontWeight.Medium,
          color = if (isInvisible) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
          )
          Surface(
            shape = RoundedCornerShape(999.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
          ) {
            Text(
              text = badge,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            )
          }
          if (usageBadge != null) {
            Surface(
              shape = RoundedCornerShape(999.dp),
              color = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
              Text(
                text = usageBadge,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onShare, modifier = Modifier.size(48.dp)) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
          )
        }
        trailingAction?.invoke()
        FilledTonalIconButton(onClick = onCopy, modifier = Modifier.size(48.dp)) {
          Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyVaultCard(
  title: String,
  subtitle: String,
  ctaLabel: String,
  onCtaClick: () -> Unit,
) {
  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Image(
        painter = painterResource(id = R.drawable.img_empty_vault),
        contentDescription = "Empty vault notebook illustration",
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(148.dp)
          .clip(MaterialTheme.shapes.large),
      )
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
      Button(
        onClick = onCtaClick,
        modifier = Modifier.height(48.dp),
      ) {
        Text(ctaLabel)
      }
    }
  }
}

@Composable
private fun NewSnippetDialog(
  onDismiss: () -> Unit,
  onSave: (String, String) -> Unit,
) {
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Save Custom Snippet") },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Label (e.g. TikTok Bio)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Symbols or text") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(title.ifBlank { "Custom Snippet" }, content) },
        enabled = content.isNotBlank(),
      ) {
        Text("Save")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    },
  )
}
