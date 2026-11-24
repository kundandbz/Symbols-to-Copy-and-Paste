package com.example.model

import java.text.BreakIterator

data class BioFrameTemplate(
  val id: String,
  val name: String,
  val category: String,
  val prefix: String,
  val suffix: String,
) {
  fun wrap(text: String): String {
    val clean = text.trim().ifEmpty { "Symbols" }
    return "$prefix$clean$suffix"
  }
}

data class BioLayoutPreset(
  val id: String,
  val title: String,
  val subtitle: String,
  val content: String,
)

object UnicodeTextUtils {

  private val invisibleCodePoints: Set<Int> = setOf(
    0x00A0, // No-Break Space
    0x115F, // Hangul Choseong Filler
    0x1160, // Hangul Jungseong Filler
    0x200B, // Zero Width Space
    0x200C, // Zero Width Non-Joiner
    0x200D, // Zero Width Joiner
    0x200E, // Left-to-Right Mark
    0x200F, // Right-to-Left Mark
    0x2060, // Word Joiner
    0x2800, // Braille Pattern Blank
    0x3164, // Hangul Filler
    0xFEFF, // Zero Width No-Break Space
    0xFFA0, // Halfwidth Hangul Filler
  )

  fun splitGraphemes(input: String): List<String> {
    if (input.isEmpty()) return emptyList()
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(input)
    val result = mutableListOf<String>()
    var start = iterator.first()
    var end = iterator.next()
    while (end != BreakIterator.DONE) {
      result.add(input.substring(start, end))
      start = end
      end = iterator.next()
    }
    return result
  }

  fun reverseGraphemes(input: String): String {
    if (input.length <= 1) return input
    return splitGraphemes(input).asReversed().joinToString("")
  }

  fun removeLastGrapheme(input: String): String {
    if (input.isEmpty()) return input
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(input)
    val end = iterator.last()
    val prev = iterator.previous()
    return if (prev != BreakIterator.DONE && prev >= 0 && prev < end) {
      input.substring(0, prev)
    } else {
      ""
    }
  }

  fun isInvisibleText(text: String): Boolean {
    if (text.isEmpty()) return false
    var i = 0
    while (i < text.length) {
      val cp = text.codePointAt(i)
      val type = Character.getType(cp)
      val isInvisibleCp = Character.isWhitespace(cp) ||
        Character.isSpaceChar(cp) ||
        type == Character.FORMAT.toInt() ||
        type == Character.CONTROL.toInt() ||
        cp in invisibleCodePoints
      if (!isInvisibleCp) return false
      i += Character.charCount(cp)
    }
    return true
  }

  fun formatVisiblePreview(text: String, fallbackLabel: String = "Invisible Text"): String {
    if (!isInvisibleText(text)) return text
    val lineCount = text.count { it == '\n' } + 1
    val cpCount = text.codePointCount(0, text.length)
    return if (lineCount > 1) {
      "[Invisible · $lineCount blank lines]"
    } else {
      "[$fallbackLabel · $cpCount char${if (cpCount == 1) "" else "s"}]"
    }
  }
}

object BioToolsEngine {

  const val BRAILLE_BLANK_LINE = "\u2800"

  val frames: List<BioFrameTemplate> = listOf(
    BioFrameTemplate("wings_royal", "Royal Gamer Wings", "Gamer", "꧁༒ ", " ༒꧂"),
    BioFrameTemplate("wings_heart", "Heart Wings", "Aesthetic", "𓆩♡ ", " ♡𓆪"),
    BioFrameTemplate("sparkle_burst", "Sparkle Burst", "Aesthetic", "✧･ﾟ: *✧･ﾟ:* ", " *:･ﾟ✧*:･ﾟ✧"),
    BioFrameTemplate("star_trail", "Shooting Star", "Aesthetic", "★彡 ", " 彡★"),
    BioFrameTemplate("ribbon_tag", "Editorial Tag", "Clean", "˗ˏˋ ★ ", " ★ ˎˊ˗"),
    BioFrameTemplate("jp_brackets", "Double Brackets", "Minimal", "『 ", " 』"),
    BioFrameTemplate("corner_frame", "Bold Frame", "Gamer", "【 ", " 】"),
    BioFrameTemplate("floral_crown", "Floral Crown", "Nature", "✿˖˚ ", " ˚˖✿"),
    BioFrameTemplate("clan_swords", "Clan Blades", "Gamer", "⚔️亗 ", " 亗⚔️"),
    BioFrameTemplate("crown_king", "Sovereign Crown", "Gamer", "♛ ", " ♛"),
    BioFrameTemplate("music_vibe", "Soundtrack", "Music", "♫₊˚.🎧 ✩｡ ", " ｡✩"),
    BioFrameTemplate("divider_wrap", "Minimal Line", "Clean", "─── ⋆⋅☆⋅⋆ ", " ⋆⋅☆⋅⋆ ───"),
  )

  val bioPresets: List<BioLayoutPreset> = listOf(
    BioLayoutPreset(
      id = "ig_minimal",
      title = "Minimal Aesthetic Bio",
      subtitle = "Clean bullet hierarchy for Instagram & TikTok",
      content = """
        ✧ 𝗰𝗿𝗲𝗮𝘁𝗶𝘃𝗲 𝗱𝗶𝗿𝗲𝗰𝘁𝗼𝗿
        📍 𝘀𝗲𝗼𝘂𝗹 · 𝘁𝗼𝗸𝘆𝗼
        ♡ 𝗱𝗲𝘀𝗶𝗴𝗻, 𝗰𝗼𝗳𝗳𝗲𝗲 & 𝗳𝗶𝗹𝗺
        ╰┈➤ linktr.ee/yourhandle
      """.trimIndent(),
    ),
    BioLayoutPreset(
      id = "gamer_card",
      title = "Pro Clan Profile Card",
      subtitle = "Sharp bracketed layout for Discord & Esports",
      content = """
        ╔═════ ∘◦ 亗 ◦∘ ═════╗
        ⚔️ 𝐂𝐋𝐀𝐍: 𝐍𝐄𝐗𝐔𝐒
        ★ 𝐑𝐀𝐍𝐊: 𝐌𝐘𝐓𝐇𝐈𝐂
        ⌖ 𝐈𝐃: 0000-0000
        ╚═════ ∘◦ 亗 ◦∘ ═════╝
      """.trimIndent(),
    ),
    BioLayoutPreset(
      id = "soft_journal",
      title = "Soft Sparkle Journal",
      subtitle = "Warm cozy framing for personal notes & bios",
      content = """
        ╭┈─────── ೄྀ࿐ ˊˎ-
        ╰┈➤ 𝘄𝗲𝗹𝗰𝗼𝗺𝗲 𝘁𝗼 𝗺𝘆 𝘀𝗽𝗮𝗰𝗲 ✿
        ⋆｡˚ ☁︎ ˚｡⋆｡˚☽˚｡⋆
        ✉ 𝗱𝗺𝘀 𝗼𝗽𝗲𝗻 𝗳𝗼𝗿 𝗰𝗼𝗹𝗹𝗮𝗯𝘀
      """.trimIndent(),
    ),
    BioLayoutPreset(
      id = "music_status",
      title = "Now Playing Status",
      subtitle = "Clean audio player aesthetic for Discord status",
      content = """
        ılı.lıllılı.ıllı.
        ɴᴏᴡ ᴘʟᴀʏɪɴɢ: 𝐌𝐢𝐝𝐧𝐢𝐠𝐡𝐭 𝐒𝐲𝐧𝐭𝐡
        1:24 ━━━━◉─────── 3:48
        ◄◄⠀▐▐ ⠀►► ⠀⠀───○ 🔊
      """.trimIndent(),
    ),
  )

  private val morseMap = mapOf(
    'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".",
    'F' to "..-.", 'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---",
    'K' to "-.-", 'L' to ".-..", 'M' to "--", 'N' to "-.", 'O' to "---",
    'P' to ".--.", 'Q' to "--.-", 'R' to ".-.", 'S' to "...", 'T' to "-",
    'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-", 'Y' to "-.--",
    'Z' to "--..",
    '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
    '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
    '.' to ".-.-.-", ',' to "--..--", '?' to "..--..", '!' to "-.-.--", '/' to "-..-.",
  )

  private val reverseMorseMap = morseMap.entries.associate { (k, v) -> v to k }

  fun toMorse(input: String): String {
    if (input.isBlank()) return ""
    return input.uppercase().trim().split(Regex("\\s+"))
      .map { word -> word.mapNotNull { morseMap[it] }.joinToString(" ") }
      .filter { it.isNotEmpty() }
      .joinToString(" / ")
  }

  fun fromMorse(morse: String): String {
    if (morse.isBlank()) return ""
    return morse.trim().split("/")
      .map { word ->
        word.trim().split(Regex("\\s+")).mapNotNull { token ->
          reverseMorseMap[token]?.toString()
        }.joinToString("")
      }
      .filter { it.isNotEmpty() }
      .joinToString(" ")
  }

  fun toSpacedAesthetic(input: String): String {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return ""
    return UnicodeTextUtils.splitGraphemes(trimmed).joinToString(" ")
  }

  fun toReversedText(input: String): String {
    return UnicodeTextUtils.reverseGraphemes(input)
  }

  fun makeInstagramSpacerBlock(lineCount: Int = 3): String {
    return List(lineCount.coerceIn(1, 10)) { BRAILLE_BLANK_LINE }.joinToString("\n")
  }
}
