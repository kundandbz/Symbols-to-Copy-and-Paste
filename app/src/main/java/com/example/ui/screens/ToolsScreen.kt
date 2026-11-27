package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BioFrameTemplate
import com.example.model.BioToolsEngine
import com.example.model.UnicodeFontEngine
import com.example.ui.ToolTab
import com.example.ui.ToolsUiState
import com.example.ui.theme.JetBrainsMonoFamily

private val fontPresets = listOf("Symbols to Copy", "Aesthetic", "Pro Gamer", "Dreamer", "VIP 2026")

private val invisibleChars = listOf(
  Triple("Hangul Filler (Blank Name)", "U+3164 · Works for invisible gamer & Discord names", "\u3164"),
  Triple("Braille Blank Character", "U+2800 · Works for blank WhatsApp messages & bio lines", "\u2800"),
  Triple("Zero-Width Space", "U+200B · Invisible separator with zero width", "\u200B"),
  Triple("Invisible Plus Blank (x5)", "5x Hangul Filler for wide blank messages", "\u3164\u3164\u3164\u3164\u3164"),
)

@Composable
fun ToolsScreen(
  state: ToolsUiState,
  onSelectToolTab: (ToolTab) -> Unit,
  onFontInputChange: (String) -> Unit,
  onSelectFontFrame: (BioFrameTemplate?) -> Unit,
  onBioInputChange: (String) -> Unit,
  onSpacerLineCountChange: (Int) -> Unit,
  onConverterInputChange: (String) -> Unit,
  onToggleMorseDecode: (Boolean) -> Unit,
  onCopyText: (String, String) -> Unit,
  onSaveSnippet: (String, String, String) -> Unit,
  onShareText: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val frameChoices = remember { BioToolsEngine.frames.take(6) }

  LazyColumn(
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.fillMaxSize(),
  ) {
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Text Tools & Generators",
            style = MaterialTheme.typography.headlineSmall,
          )
          Text(
            text = "Fancy Unicode fonts, gamer & bio frames, invisible text, and converters.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(ToolTab.entries, key = { it.name }) { tab ->
            val selected = state.activeTool == tab
            FilterChip(
              selected = selected,
              onClick = { onSelectToolTab(tab) },
              label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
              ),
              modifier = Modifier.testTag("tool_tab_${tab.name}"),
            )
          }
        }
      }
    }

    when (state.activeTool) {
      ToolTab.FONTS -> {
        val effectiveText = state.fontInput.ifEmpty { "Symbols to Copy" }

        item {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = state.fontInput,
              onValueChange = onFontInputChange,
              label = { Text("Your text") },
              placeholder = { Text("Type a name, bio, or headline…") },
              trailingIcon = {
                if (state.fontInput.isNotEmpty()) {
                  IconButton(onClick = { onFontInputChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear text")
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
                .testTag("fonts_text_input"),
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(fontPresets) { preset ->
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onFontInputChange(preset) },
                ) {
                  Text(
                    text = preset,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  )
                }
              }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(frameChoices, key = { it.id }) { frame ->
                val isSelected = state.activeFontFrame?.id == frame.id
                FilterChip(
                  selected = isSelected,
                  onClick = { onSelectFontFrame(frame) },
                  label = { Text("${frame.prefix.trim()} Aa ${frame.suffix.trim()}") },
                )
              }
            }
          }
        }

        items(UnicodeFontEngine.styles, key = { it.id }, contentType = { "font_card" }) { style ->
          val styledCore = remember(effectiveText, style.id) { style.transform(effectiveText) }
          val finalOutput = remember(styledCore, state.activeFontFrame) {
            state.activeFontFrame?.let { "${it.prefix}$styledCore${it.suffix}" } ?: styledCore
          }

          Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.large)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(finalOutput, style.name)
              }
              .testTag("font_card_${style.id}"),
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Text(
                  text = style.name,
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                )
                Surface(
                  shape = RoundedCornerShape(999.dp),
                  color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                ) {
                  Text(
                    text = style.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                  )
                }
              }
              Text(
                text = finalOutput,
                fontSize = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                IconButton(onClick = { onShareText(finalOutput) }, modifier = Modifier.size(48.dp)) {
                  Icon(Icons.Outlined.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                }
                IconButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSaveSnippet(style.name, finalOutput, "Unicode Font")
                  },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save font", modifier = Modifier.size(20.dp))
                }
                FilledTonalIconButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCopyText(finalOutput, style.name)
                  },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }
      }

      ToolTab.BIO_TAGS -> {
        item {
          OutlinedTextField(
            value = state.bioInput,
            onValueChange = onBioInputChange,
            label = { Text("Nickname, Handle, or Clan Name") },
            placeholder = { Text("Enter name to decorate with wings & frames…") },
            trailingIcon = {
              if (state.bioInput.isNotEmpty()) {
                IconButton(onClick = { onBioInputChange("") }) {
                  Icon(Icons.Filled.Clear, contentDescription = "Clear name")
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
              .testTag("studio_bio_input"),
          )
        }

        items(BioToolsEngine.frames, key = { it.id }, contentType = { "bio_frame" }) { frame ->
          val wrapped = remember(state.bioInput, frame.id) { frame.wrap(state.bioInput) }
          Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
              .fillMaxWidth()
              .clip(MaterialTheme.shapes.medium)
              .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(wrapped, frame.name)
              },
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = wrapped,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Medium,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
                Text(
                  text = "${frame.name} · ${frame.category}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSaveSnippet(frame.name, wrapped, "Bio Tag")
                  },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save framed tag", modifier = Modifier.size(20.dp))
                }
                FilledTonalIconButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCopyText(wrapped, frame.name)
                  },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(Icons.Filled.ContentCopy, contentDescription = "Copy framed tag", modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }

        item {
          Text(
            text = "Ready-to-Copy Bio Templates",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 6.dp),
          )
        }

        items(BioToolsEngine.bioPresets, key = { it.id }) { preset ->
          Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Column {
                Text(text = preset.title, style = MaterialTheme.typography.titleMedium)
                Text(
                  text = preset.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = preset.content,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(12.dp),
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                IconButton(onClick = { onShareText(preset.content) }, modifier = Modifier.size(48.dp)) {
                  Icon(Icons.Outlined.Share, contentDescription = "Share template")
                }
                IconButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSaveSnippet(preset.title, preset.content, "Bio Template")
                  },
                  modifier = Modifier.size(48.dp),
                ) {
                  Icon(Icons.Filled.BookmarkAdd, contentDescription = "Save template")
                }
                Spacer(modifier = Modifier.width(4.dp))
                FilledTonalButton(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCopyText(preset.content, preset.title)
                  },
                  modifier = Modifier.height(48.dp),
                ) {
                  Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Copy Bio")
                }
              }
            }
          }
        }
      }

      ToolTab.INVISIBLE -> {
        item {
          Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              Text(
                text = "Instagram & TikTok Blank Line Spacer",
                style = MaterialTheme.typography.titleMedium,
              )
              Text(
                text = "Uses Unicode Braille Pattern Blank (U+2800) so social media bios preserve empty vertical line breaks.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  IconButton(
                    onClick = { onSpacerLineCountChange(state.spacerLineCount - 1) },
                    enabled = state.spacerLineCount > 1,
                    modifier = Modifier.size(48.dp),
                  ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease blank lines")
                  }
                  Text(
                    text = "${state.spacerLineCount} line${if (state.spacerLineCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelLarge,
                  )
                  IconButton(
                    onClick = { onSpacerLineCountChange(state.spacerLineCount + 1) },
                    enabled = state.spacerLineCount < 8,
                    modifier = Modifier.size(48.dp),
                  ) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase blank lines")
                  }
                }
                Button(
                  onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onCopyText(
                      BioToolsEngine.makeInstagramSpacerBlock(state.spacerLineCount),
                      "Invisible Line Spacer",
                    )
                  },
                  modifier = Modifier
                    .height(48.dp)
                    .testTag("copy_spacer_button"),
                ) {
                  Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Copy Spacer")
                }
              }
            }
          }
        }

        items(invisibleChars, key = { it.first }, contentType = { "invisible_char" }) { (title, desc, payload) ->
          Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = desc,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              FilledTonalButton(
                onClick = {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  onCopyText(payload, title)
                },
                modifier = Modifier.height(48.dp),
              ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy")
              }
            }
          }
        }
      }

      ToolTab.CONVERTERS -> {
        item {
          OutlinedTextField(
            value = state.converterInput,
            onValueChange = onConverterInputChange,
            label = { Text(if (state.isMorseDecode) "Text or Morse (.- / -...)" else "Input Text") },
            placeholder = { Text("Type words or Morse code…") },
            trailingIcon = {
              if (state.converterInput.isNotEmpty()) {
                IconButton(onClick = { onConverterInputChange("") }) {
                  Icon(Icons.Filled.Clear, contentDescription = "Clear input")
                }
              }
            },
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("studio_converter_input"),
          )
        }

        item {
          val morseResult = remember(state.converterInput, state.isMorseDecode) {
            if (state.isMorseDecode) BioToolsEngine.fromMorse(state.converterInput)
            else BioToolsEngine.toMorse(state.converterInput)
          }
          ToolConverterCard(
            title = if (state.isMorseDecode) "Morse → Plain Text" else "Plain Text → Morse Code",
            subtitle = "International ITU Morse encoding with slash word breaks",
            output = morseResult.ifEmpty { "Type letters or Morse code above" },
            isMono = !state.isMorseDecode,
            copyEnabled = morseResult.isNotBlank(),
            actionLabel = if (state.isMorseDecode) "Encode" else "Decode",
            onSecondaryAction = { onToggleMorseDecode(!state.isMorseDecode) },
            onCopy = {
              if (morseResult.isNotBlank()) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCopyText(morseResult, "Morse Code")
              }
            },
            onShare = { if (morseResult.isNotBlank()) onShareText(morseResult) },
          )
        }

        item {
          val spaced = remember(state.converterInput) {
            val fullwidth = UnicodeFontEngine.styles.firstOrNull { it.id == "fullwidth" }
            val base = state.converterInput.ifEmpty { "AESTHETIC" }
            fullwidth?.transform(base) ?: BioToolsEngine.toSpacedAesthetic(base)
          }
          ToolConverterCard(
            title = "Vaporwave Aesthetic Width",
            subtitle = "Full-width retro typography for bios & captions",
            output = spaced,
            isMono = false,
            onCopy = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onCopyText(spaced, "Vaporwave Text")
            },
            onShare = { onShareText(spaced) },
          )
        }

        item {
          val spacedLetters = remember(state.converterInput) {
            BioToolsEngine.toSpacedAesthetic(state.converterInput.ifEmpty { "SYMBOLS" })
          }
          ToolConverterCard(
            title = "Editorial Letter-Spaced",
            subtitle = "Airy spaced characters for headers & captions",
            output = spacedLetters,
            isMono = false,
            onCopy = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onCopyText(spacedLetters, "Letter-Spaced Text")
            },
            onShare = { onShareText(spacedLetters) },
          )
        }

        item {
          val reversed = remember(state.converterInput) {
            BioToolsEngine.toReversedText(state.converterInput.ifEmpty { "HELLO" })
          }
          ToolConverterCard(
            title = "Reverse Text",
            subtitle = "Backwards character mirror",
            output = reversed,
            isMono = false,
            onCopy = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onCopyText(reversed, "Reversed Text")
            },
            onShare = { onShareText(reversed) },
          )
        }
      }
    }
  }
}

@Composable
private fun ToolConverterCard(
  title: String,
  subtitle: String,
  output: String,
  isMono: Boolean,
  copyEnabled: Boolean = true,
  actionLabel: String? = null,
  onSecondaryAction: (() -> Unit)? = null,
  onCopy: () -> Unit,
  onShare: () -> Unit,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(text = title, style = MaterialTheme.typography.titleMedium)
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        if (actionLabel != null && onSecondaryAction != null) {
          FilledTonalButton(
            onClick = onSecondaryAction,
            modifier = Modifier.height(48.dp),
          ) {
            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(actionLabel, style = MaterialTheme.typography.labelMedium)
          }
        }
      }

      Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = output,
          fontFamily = if (isMono) JetBrainsMonoFamily else MaterialTheme.typography.bodyLarge.fontFamily,
          fontSize = 18.sp,
          modifier = Modifier.padding(14.dp),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(
          onClick = onShare,
          enabled = copyEnabled,
          modifier = Modifier.size(48.dp),
        ) {
          Icon(Icons.Outlined.Share, contentDescription = "Share result")
        }
        Spacer(modifier = Modifier.width(6.dp))
        Button(
          onClick = onCopy,
          enabled = copyEnabled,
          modifier = Modifier.height(48.dp),
        ) {
          Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Copy")
        }
      }
    }
  }
}
