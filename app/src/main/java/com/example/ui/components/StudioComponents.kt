package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import com.example.model.UnicodeTextUtils
import com.example.ui.BannerTone
import com.example.ui.CopyBannerState
import com.example.ui.theme.JetBrainsMonoFamily

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FloatingCopyBanner(
  banner: CopyBannerState?,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    visible = banner != null,
    enter = slideInVertically(
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
      ),
      initialOffsetY = { it },
    ) + scaleIn(
      initialScale = 0.88f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
      ),
    ) + fadeIn(animationSpec = tween(150)),
    exit = slideOutVertically(
      animationSpec = tween(200),
      targetOffsetY = { it },
    ) + scaleOut(targetScale = 0.92f, animationSpec = tween(180)) + fadeOut(animationSpec = tween(180)),
    modifier = modifier,
  ) {
    banner?.let { state ->
      val isInvisible = UnicodeTextUtils.isInvisibleText(state.text)
      val cleanPreview = state.text.replace("\n", " ")
      val message = when (state.tone) {
        BannerTone.COPIED -> if (isInvisible) "Copied ${state.label}" else "Copied “$cleanPreview”"
        BannerTone.ADDED -> "Added “$cleanPreview” to Tray"
        BannerTone.SAVED -> "Saved “${state.label}” to Vault"
        BannerTone.FAVORITED -> "Added “$cleanPreview” to Favorites"
        BannerTone.UNFAVORITED -> "Removed “$cleanPreview” from Favorites"
      }
      val icon = when (state.tone) {
        BannerTone.COPIED -> Icons.Filled.CheckCircle
        BannerTone.ADDED -> Icons.Filled.Add
        BannerTone.SAVED -> Icons.Filled.BookmarkAdded
        BannerTone.FAVORITED -> Icons.Filled.Favorite
        BannerTone.UNFAVORITED -> Icons.Outlined.FavoriteBorder
      }
      val iconDesc = when (state.tone) {
        BannerTone.COPIED -> "Copied"
        BannerTone.ADDED -> "Added to tray"
        BannerTone.SAVED -> "Saved to vault"
        BannerTone.FAVORITED -> "Added to favorites"
        BannerTone.UNFAVORITED -> "Removed from favorites"
      }
      val iconTint = when (state.tone) {
        BannerTone.FAVORITED -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
      }

      val iconScale = remember { Animatable(1f) }
      val progress = remember { Animatable(1f) }

      LaunchedEffect(state.id) {
        iconScale.snapTo(0.4f)
        progress.snapTo(1f)
        launch {
          iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow,
            ),
          )
        }
        launch {
          progress.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing),
          )
        }
      }

      Surface(
        onClick = onDismiss,
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.onBackground,
        contentColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        modifier = Modifier
          .widthIn(max = 420.dp)
          .padding(horizontal = 16.dp)
          .testTag("floating_copy_banner"),
      ) {
        Column(
          modifier = Modifier
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(999.dp)),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Icon(
              imageVector = icon,
              contentDescription = iconDesc,
              tint = iconTint,
              modifier = Modifier
                .size(22.dp)
                .graphicsLayer {
                  scaleX = iconScale.value
                  scaleY = iconScale.value
                },
            )
            AnimatedContent(
              targetState = state.id to message,
              transitionSpec = {
                (fadeIn(tween(150)) + slideInVertically(tween(180)) { it / 3 }) togetherWith
                  (fadeOut(tween(100)) + slideOutVertically(tween(140)) { -it / 3 })
              },
              label = "copy_banner_text",
              modifier = Modifier.weight(1f, fill = false),
            ) { (_, text) ->
              Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(2.5.dp)
              .background(MaterialTheme.colorScheme.background.copy(alpha = 0.12f)),
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(fraction = progress.value.coerceIn(0f, 1f))
                .height(2.5.dp)
                .background(MaterialTheme.colorScheme.secondary),
            )
          }
        }
      }
    }
  }
}

@Composable
fun CompactBuildTrayBar(
  composerText: String,
  isBuildMode: Boolean,
  onBackspace: () -> Unit,
  onCopyAll: () -> Unit,
  onExitBuildMode: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.secondaryContainer,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("compact_build_tray_bar"),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = if (isBuildMode) "Build Mode Tray · Tap items to append" else "Combo Tray",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = composerText.ifEmpty { "Tap any emoji, kaomoji, or face…" },
          style = MaterialTheme.typography.titleSmall,
          color = if (composerText.isEmpty()) {
            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
          } else {
            MaterialTheme.colorScheme.onSecondaryContainer
          },
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      if (composerText.isNotEmpty()) {
        IconButton(
          onClick = onBackspace,
          modifier = Modifier.size(48.dp),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Backspace tray",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(18.dp),
          )
        }
        IconButton(
          onClick = onCopyAll,
          modifier = Modifier
            .size(48.dp)
            .testTag("compact_tray_copy_button"),
        ) {
          Icon(
            imageVector = Icons.Filled.ContentCopy,
            contentDescription = "Copy tray text",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
          )
        }
      }
      IconButton(
        onClick = onExitBuildMode,
        modifier = Modifier
          .size(48.dp)
          .testTag("compact_tray_close_button"),
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = "Turn off build mode",
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CatalogGlyphTile(
  item: CatalogEntry,
  isFavorite: Boolean,
  isBuildMode: Boolean,
  onTap: () -> Unit,
  onLongPress: () -> Unit,
  onToggleFavorite: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val isEmoji = item.section == CatalogSection.EMOJIS
  var tapPulseCount by remember(item.id) { mutableIntStateOf(0) }
  var showCopiedFeedback by remember(item.id) { mutableStateOf(false) }
  val tileScale = remember(item.id) { Animatable(1f) }

  LaunchedEffect(tapPulseCount) {
    if (tapPulseCount > 0) {
      showCopiedFeedback = true
      launch {
        tileScale.animateTo(0.93f, animationSpec = tween(durationMillis = 70))
        tileScale.animateTo(
          targetValue = 1f,
          animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
          ),
        )
      }
      delay(950)
      showCopiedFeedback = false
    }
  }

  val accentColor = if (isBuildMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
  val accentOnColor = if (isBuildMode) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
  val surfaceTint by animateColorAsState(
    targetValue = if (showCopiedFeedback) {
      if (isBuildMode) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
      else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
    } else {
      MaterialTheme.colorScheme.surfaceContainerLowest
    },
    animationSpec = tween(durationMillis = 200),
    label = "tile_surface_tint",
  )
  val borderColor by animateColorAsState(
    targetValue = when {
      showCopiedFeedback -> accentColor
      isFavorite -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)
      else -> MaterialTheme.colorScheme.outlineVariant
    },
    animationSpec = tween(durationMillis = 200),
    label = "tile_border_color",
  )

  Surface(
    shape = MaterialTheme.shapes.medium,
    color = surfaceTint,
    border = BorderStroke(
      width = if (showCopiedFeedback) 1.5.dp else 1.dp,
      color = borderColor,
    ),
    tonalElevation = if (showCopiedFeedback) 3.dp else 1.dp,
    modifier = modifier
      .graphicsLayer {
        scaleX = tileScale.value
        scaleY = tileScale.value
      }
      .clip(MaterialTheme.shapes.medium)
      .combinedClickable(
        onClick = {
          tapPulseCount++
          onTap()
        },
        onLongClick = onLongPress,
      )
      .testTag("catalog_tile_${item.id}"),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(
          start = if (item.isWide) 14.dp else 8.dp,
          end = if (item.isWide) 14.dp else 6.dp,
          top = if (item.isWide) 14.dp else 6.dp,
          bottom = if (item.isWide) 14.dp else 10.dp,
        ),
    ) {
      if (item.isWide) {
        val actionBgColor by animateColorAsState(
          targetValue = when {
            showCopiedFeedback -> accentColor
            isBuildMode -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.primaryContainer
          },
          animationSpec = tween(durationMillis = 180),
          label = "wide_action_bg",
        )
        val actionIconTint by animateColorAsState(
          targetValue = when {
            showCopiedFeedback -> accentOnColor
            isBuildMode -> MaterialTheme.colorScheme.onSecondaryContainer
            else -> MaterialTheme.colorScheme.onPrimaryContainer
          },
          animationSpec = tween(durationMillis = 180),
          label = "wide_action_tint",
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.glyph,
              fontSize = 18.sp,
              fontWeight = FontWeight.Medium,
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 3,
              overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            AnimatedContent(
              targetState = showCopiedFeedback,
              transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(120)) },
              label = "wide_subtitle",
            ) { copied ->
              Text(
                text = if (copied) {
                  if (isBuildMode) "Added to Tray!" else "Copied to clipboard!"
                } else {
                  "${item.name} · ${item.categoryLabel}"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (copied) FontWeight.SemiBold else FontWeight.Normal,
                color = if (copied) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onToggleFavorite,
              modifier = Modifier
                .size(48.dp)
                .testTag("fav_btn_${item.id}"),
            ) {
              Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
              )
            }
            Surface(
              shape = CircleShape,
              color = actionBgColor,
              modifier = Modifier
                .size(36.dp)
                .then(
                  if (showCopiedFeedback) Modifier.testTag("tile_copied_badge_${item.id}")
                  else Modifier
                ),
            ) {
              Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                  targetState = showCopiedFeedback,
                  transitionSpec = {
                    (scaleIn(
                      initialScale = 0.4f,
                      animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                      ),
                    ) + fadeIn(tween(120))) togetherWith (scaleOut(targetScale = 0.5f, animationSpec = tween(100)) + fadeOut(tween(100)))
                  },
                  label = "wide_copy_icon",
                ) { copied ->
                  Icon(
                    imageVector = when {
                      copied -> Icons.Filled.Check
                      isBuildMode -> Icons.Filled.Add
                      else -> Icons.Filled.ContentCopy
                    },
                    contentDescription = when {
                      copied -> if (isBuildMode) "Added" else "Copied"
                      isBuildMode -> "Append to tray"
                      else -> "Copy item"
                    },
                    tint = actionIconTint,
                    modifier = Modifier.size(18.dp),
                  )
                }
              }
            }
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              text = if (isEmoji) item.categoryLabel.substringBefore(" ") else item.codepointSummary.substringBefore(" "),
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.weight(1f, fill = false),
            )
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .clickable(onClick = onToggleFavorite)
                .testTag("fav_btn_${item.id}"),
            ) {
              androidx.compose.animation.AnimatedVisibility(
                visible = showCopiedFeedback,
                enter = scaleIn(
                  initialScale = 0.3f,
                  animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                  ),
                ) + fadeIn(tween(120)),
                exit = scaleOut(targetScale = 0.4f, animationSpec = tween(120)) + fadeOut(tween(120)),
              ) {
                Surface(
                  shape = CircleShape,
                  color = accentColor,
                  modifier = Modifier
                    .size(18.dp)
                    .testTag("tile_copied_badge_${item.id}"),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.Check,
                      contentDescription = if (isBuildMode) "Added to tray" else "Copied",
                      tint = accentOnColor,
                      modifier = Modifier.size(12.dp),
                    )
                  }
                }
              }
              if (!showCopiedFeedback) {
                Icon(
                  imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                  contentDescription = if (isFavorite) "Remove ${item.name} from favorites" else "Add ${item.name} to favorites",
                  tint = if (isFavorite) {
                    MaterialTheme.colorScheme.tertiary
                  } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                  },
                  modifier = Modifier.size(14.dp),
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = item.glyph,
            fontSize = if (isEmoji) 30.sp else 26.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
          )
          Spacer(modifier = Modifier.height(6.dp))
          AnimatedContent(
            targetState = showCopiedFeedback,
            transitionSpec = { fadeIn(tween(140)) togetherWith fadeOut(tween(100)) },
            label = "compact_tile_label",
          ) { copied ->
            Text(
              text = if (copied) {
                if (isBuildMode) "Added!" else "Copied!"
              } else {
                item.name
              },
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
              fontWeight = if (copied) FontWeight.Bold else FontWeight.Normal,
              color = if (copied) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogDetailSheet(
  item: CatalogEntry,
  isFavorite: Boolean,
  onDismiss: () -> Unit,
  onCopy: () -> Unit,
  onAppendToTray: () -> Unit,
  onToggleFavorite: () -> Unit,
  onShare: () -> Unit,
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
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp, horizontal = 16.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = item.glyph,
            fontSize = if (item.isWide) 28.sp else 64.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Text(
        text = item.name,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
          Text(
            text = "${item.section.label} · ${item.categoryLabel}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
        Surface(
          shape = RoundedCornerShape(999.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
          Text(
            text = item.codepointSummary,
            fontFamily = JetBrainsMonoFamily,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Outlined.Info,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(15.dp),
        )
        Text(
          text = "Tags: ${item.tags.distinct().take(5).joinToString(" · ")}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Spacer(modifier = Modifier.height(22.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Button(
          onClick = {
            onCopy()
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("detail_copy_button"),
        ) {
          Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Copy")
        }

        FilledTonalButton(
          onClick = {
            onAppendToTray()
            onDismiss()
          },
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("detail_append_button"),
        ) {
          Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add to Tray")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("detail_favorite_button"),
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (isFavorite) "Saved" else "Favorite")
        }

        OutlinedButton(
          onClick = onShare,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("detail_share_button"),
        ) {
          Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Share")
        }
      }
    }
  }
}
