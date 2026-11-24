package com.example.model

data class UnicodeFontStyle(
  val id: String,
  val name: String,
  val tag: String,
  val transform: (String) -> String,
)

object UnicodeFontEngine {

  private fun cp(codePoint: Int): String = String(Character.toChars(codePoint))

  private fun mapAlphabetAndDigits(
    input: String,
    upperBase: Int,
    lowerBase: Int,
    digitBase: Int? = null,
    exceptions: Map<Char, String> = emptyMap(),
  ): String {
    val sb = StringBuilder(input.length * 2)
    for (ch in input) {
      val override = exceptions[ch]
      when {
        override != null -> sb.append(override)
        ch in 'A'..'Z' -> sb.append(cp(upperBase + (ch - 'A')))
        ch in 'a'..'z' -> sb.append(cp(lowerBase + (ch - 'a')))
        digitBase != null && ch in '0'..'9' -> sb.append(cp(digitBase + (ch - '0')))
        else -> sb.append(ch)
      }
    }
    return sb.toString()
  }

  private fun mapByTable(input: String, table: Map<Char, String>): String {
    val sb = StringBuilder(input.length * 2)
    for (ch in input) {
      sb.append(table[ch] ?: ch.toString())
    }
    return sb.toString()
  }

  private fun appendCombining(input: String, combiningChar: Char): String {
    val sb = StringBuilder(input.length * 2)
    for (cluster in UnicodeTextUtils.splitGraphemes(input)) {
      sb.append(cluster)
      if (cluster.isNotBlank()) {
        sb.append(combiningChar)
      }
    }
    return sb.toString()
  }

  private val scriptLightExceptions = mapOf(
    'B' to "ℬ", 'E' to "ℰ", 'F' to "ℱ", 'H' to "ℋ", 'I' to "ℐ",
    'L' to "ℒ", 'M' to "ℳ", 'R' to "ℛ", 'e' to "ℯ", 'g' to "ℊ", 'o' to "ℴ",
  )

  private val frakturExceptions = mapOf(
    'C' to "ℭ", 'H' to "ℌ", 'I' to "ℑ", 'R' to "ℜ", 'Z' to "ℨ",
  )

  private val doubleStruckExceptions = mapOf(
    'C' to "ℂ", 'H' to "ℍ", 'N' to "ℕ", 'P' to "ℙ", 'Q' to "ℚ", 'R' to "ℝ", 'Z' to "ℤ",
  )

  private val smallCapsTable: Map<Char, String> = mapOf(
    'a' to "ᴀ", 'b' to "ʙ", 'c' to "ᴄ", 'd' to "ᴅ", 'e' to "ᴇ", 'f' to "ꜰ",
    'g' to "ɢ", 'h' to "ʜ", 'i' to "ɪ", 'j' to "ᴊ", 'k' to "ᴋ", 'l' to "ʟ",
    'm' to "ᴍ", 'n' to "ɴ", 'o' to "ᴏ", 'p' to "ᴘ", 'q' to "ǫ", 'r' to "ʀ",
    's' to "ꜱ", 't' to "ᴛ", 'u' to "ᴜ", 'v' to "ᴠ", 'w' to "ᴡ", 'x' to "x",
    'y' to "ʏ", 'z' to "ᴢ",
  )

  private val bubbleTable: Map<Char, String> = buildMap {
    for (i in 0..25) {
      put(('A' + i), cp(0x24B6 + i))
      put(('a' + i), cp(0x24D0 + i))
    }
    put('0', "⓪")
    for (i in 1..9) {
      put(('0' + i), cp(0x2460 + i - 1))
    }
  }

  private val blackBubbleTable: Map<Char, String> = buildMap {
    for (i in 0..25) {
      val glyph = cp(0x1F150 + i)
      put(('A' + i), glyph)
      put(('a' + i), glyph)
    }
    put('0', "⓿")
  }

  private val squareTable: Map<Char, String> = buildMap {
    for (i in 0..25) {
      val glyph = cp(0x1F130 + i)
      put(('A' + i), glyph)
      put(('a' + i), glyph)
    }
  }

  private val blackSquareTable: Map<Char, String> = buildMap {
    for (i in 0..25) {
      val glyph = cp(0x1F170 + i)
      put(('A' + i), glyph)
      put(('a' + i), glyph)
    }
  }

  private val fullWidthTable: Map<Char, String> = buildMap {
    put(' ', "\u3000")
    for (c in '!'..'~') {
      put(c, cp(0xFF01 + (c - '!')))
    }
  }

  private val upsideDownTable: Map<Char, String> = mapOf(
    'a' to "ɐ", 'b' to "q", 'c' to "ɔ", 'd' to "p", 'e' to "ǝ", 'f' to "ɟ",
    'g' to "ƃ", 'h' to "ɥ", 'i' to "ᴉ", 'j' to "ɾ", 'k' to "ʞ", 'l' to "l",
    'm' to "ɯ", 'n' to "u", 'o' to "o", 'p' to "d", 'q' to "b", 'r' to "ɹ",
    's' to "s", 't' to "ʇ", 'u' to "n", 'v' to "ʌ", 'w' to "ʍ", 'x' to "x",
    'y' to "ʎ", 'z' to "z",
    'A' to "∀", 'B' to "𐐒", 'C' to "Ɔ", 'D' to "ᗡ", 'E' to "Ǝ", 'F' to "Ⅎ",
    'G' to "פ", 'H' to "H", 'I' to "I", 'J' to "ſ", 'K' to "ʞ", 'L' to "˥",
    'M' to "W", 'N' to "N", 'O' to "O", 'P' to "Ԁ", 'Q' to "Ό", 'R' to "ᴚ",
    'S' to "S", 'T' to "⊥", 'U' to "∩", 'V' to "Λ", 'W' to "M", 'X' to "X",
    'Y' to "⅄", 'Z' to "Z",
    '1' to "Ɩ", '2' to "ᄅ", '3' to "Ɛ", '4' to "ㄣ", '5' to "ϛ",
    '6' to "9", '7' to "ㄥ", '8' to "8", '9' to "6", '0' to "0",
    '.' to "˙", ',' to "'", '?' to "¿", '!' to "¡", '\'' to ",",
    '"' to "„", '(' to ")", ')' to "(", '[' to "]", ']' to "[",
    '{' to "}", '}' to "{", '<' to ">", '>' to "<", '&' to "⅋", '_' to "‾",
  )

  private val superscriptTable: Map<Char, String> = mapOf(
    'a' to "ᵃ", 'b' to "ᵇ", 'c' to "ᶜ", 'd' to "ᵈ", 'e' to "ᵉ", 'f' to "ᶠ",
    'g' to "ᵍ", 'h' to "ʰ", 'i' to "ⁱ", 'j' to "ʲ", 'k' to "ᵏ", 'l' to "ˡ",
    'm' to "ᵐ", 'n' to "ⁿ", 'o' to "ᵒ", 'p' to "ᵖ", 'q' to "ᵠ", 'r' to "ʳ",
    's' to "ˢ", 't' to "ᵗ", 'u' to "ᵘ", 'v' to "ᵛ", 'w' to "ʷ", 'x' to "ˣ",
    'y' to "ʸ", 'z' to "ᶻ",
    '0' to "⁰", '1' to "¹", '2' to "²", '3' to "³", '4' to "⁴",
    '5' to "⁵", '6' to "⁶", '7' to "⁷", '8' to "⁸", '9' to "⁹",
  )

  val styles: List<UnicodeFontStyle> = listOf(
    UnicodeFontStyle("bold_sans", "Sans Bold", "Bio & Headings") { s ->
      mapAlphabetAndDigits(s, 0x1D5D4, 0x1D5EE, 0x1D7EC)
    },
    UnicodeFontStyle("bold_serif", "Serif Bold", "Classic") { s ->
      mapAlphabetAndDigits(s, 0x1D400, 0x1D41A, 0x1D7CE)
    },
    UnicodeFontStyle("italic_serif", "Serif Italic", "Editorial") { s ->
      mapAlphabetAndDigits(s, 0x1D434, 0x1D44E, exceptions = mapOf('h' to "ℎ"))
    },
    UnicodeFontStyle("bold_italic", "Bold Italic", "Emphasis") { s ->
      mapAlphabetAndDigits(s, 0x1D468, 0x1D482)
    },
    UnicodeFontStyle("script_bold", "Bold Cursive", "Aesthetic") { s ->
      mapAlphabetAndDigits(s, 0x1D4D0, 0x1D4EA)
    },
    UnicodeFontStyle("script_light", "Script Light", "Handwritten") { s ->
      mapAlphabetAndDigits(s, 0x1D49C, 0x1D4B6, exceptions = scriptLightExceptions)
    },
    UnicodeFontStyle("bold_fraktur", "Bold Gothic", "Gamer & Dark") { s ->
      mapAlphabetAndDigits(s, 0x1D56C, 0x1D586)
    },
    UnicodeFontStyle("fraktur", "Fraktur Gothic", "Medieval") { s ->
      mapAlphabetAndDigits(s, 0x1D504, 0x1D51E, exceptions = frakturExceptions)
    },
    UnicodeFontStyle("double_struck", "Double-Struck", "Outline") { s ->
      mapAlphabetAndDigits(s, 0x1D538, 0x1D552, 0x1D7D8, exceptions = doubleStruckExceptions)
    },
    UnicodeFontStyle("sans_italic", "Sans Italic", "Minimal") { s ->
      mapAlphabetAndDigits(s, 0x1D608, 0x1D622)
    },
    UnicodeFontStyle("sans_bold_italic", "Sans Bold Italic", "Dynamic") { s ->
      mapAlphabetAndDigits(s, 0x1D63C, 0x1D656)
    },
    UnicodeFontStyle("monospace", "Typewriter Mono", "Code & Clean") { s ->
      mapAlphabetAndDigits(s, 0x1D670, 0x1D68A, 0x1D7F6)
    },
    UnicodeFontStyle("small_caps", "Small Caps", "Profile Bio") { s ->
      mapByTable(s.lowercase(), smallCapsTable)
    },
    UnicodeFontStyle("fullwidth", "Vaporwave Wide", "Retro") { s ->
      mapByTable(s, fullWidthTable)
    },
    UnicodeFontStyle("bubble", "Bubble Letters", "Playful") { s ->
      mapByTable(s, bubbleTable)
    },
    UnicodeFontStyle("black_bubble", "Dark Bubble", "Badge") { s ->
      mapByTable(s, blackBubbleTable)
    },
    UnicodeFontStyle("square", "Square Box", "Tags") { s ->
      mapByTable(s, squareTable)
    },
    UnicodeFontStyle("black_square", "Solid Square", "Clan Tag") { s ->
      mapByTable(s, blackSquareTable)
    },
    UnicodeFontStyle("superscript", "Tiny Superscript", "Subtle") { s ->
      mapByTable(s.lowercase(), superscriptTable)
    },
    UnicodeFontStyle("strikethrough", "Strikethrough", "Edit") { s ->
      appendCombining(s, '\u0336')
    },
    UnicodeFontStyle("underline", "Underlined", "Accent") { s ->
      appendCombining(s, '\u0332')
    },
    UnicodeFontStyle("upside_down", "Upside Down", "Quirky") { s ->
      UnicodeTextUtils.splitGraphemes(s)
        .asReversed()
        .joinToString("") { cluster ->
          if (cluster.length == 1) upsideDownTable[cluster[0]] ?: cluster else cluster
        }
    },
  )
}
