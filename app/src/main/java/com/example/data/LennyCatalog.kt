package com.example.data

import com.example.model.CatalogCategory
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import com.example.model.LennyCategories

data class LennyEarPair(val left: String, val right: String, val label: String)

object LennyBuilderParts {
  val ears: List<LennyEarPair> = listOf(
    LennyEarPair("( ", " )", "Classic"),
    LennyEarPair("ʕ ", " ʔ", "Bear"),
    LennyEarPair("ᕦ( ", " )ᕤ", "Flex"),
    LennyEarPair("[ ", " ]", "Box"),
    LennyEarPair("ᕙ( ", " )ᕗ", "Strong"),
    LennyEarPair("ヽ( ", " )ﾉ", "Cheer"),
    LennyEarPair("¯\\_( ", " )_/¯", "Shrug"),
    LennyEarPair("(ノ ", " )ノ", "Throw"),
    LennyEarPair("ヾ( ", " )ﾉ", "Wave"),
  )

  val eyes: List<Pair<String, String>> = listOf(
    " ͡°" to " ͡°",
    " ͡~" to " ͡°",
    " ͡◉" to " ͡◉",
    " ͠°" to " ͠°",
    "•" to "•",
    "ಠ" to "ಠ",
    "♥" to "♥",
    "✧" to "✧",
    "¬" to "¬",
    "ʘ" to "ʘ",
    "ᵔ" to "ᵔ",
    "╥" to "╥",
  )

  val mouths: List<String> = listOf(
    " ͜ʖ ",
    " ͟ʖ ",
    " ᴥ ",
    " ◡ ",
    " 益 ",
    " ︿ ",
    " 3 ",
    " ‿ ",
    " ﹏ ",
    " ▾ ",
    " ³ ",
    "_",
  )

  val accessories: List<Pair<String, String>> = listOf(
    "" to "",
    "" to " ︵ ┻━┻",
    "" to " *:･ﾟ✧",
    "♡ " to " ♡",
    "★ " to " ★",
    "" to " ⌐■-■",
    "" to " ♪",
  )

  fun build(
    earIndex: Int,
    eyeIndex: Int,
    mouthIndex: Int,
    accessoryIndex: Int,
  ): String {
    val ear = ears[earIndex.coerceIn(0, ears.lastIndex)]
    val (leftEye, rightEye) = eyes[eyeIndex.coerceIn(0, eyes.lastIndex)]
    val mouth = mouths[mouthIndex.coerceIn(0, mouths.lastIndex)]
    val (pre, post) = accessories[accessoryIndex.coerceIn(0, accessories.lastIndex)]
    return "$pre${ear.left}$leftEye$mouth$rightEye${ear.right}$post"
  }
}

object LennyCatalog {

  private fun lenny(
    id: String,
    glyph: String,
    name: String,
    category: CatalogCategory,
    vararg tags: String,
  ) = CatalogEntry(
    id = id,
    glyph = glyph,
    name = name,
    section = CatalogSection.LENNY,
    categoryId = category.id,
    categoryLabel = category.label,
    tags = (tags.toList() + name.lowercase().split(" ") + category.label.lowercase().split(" ")).distinct(),
    isWide = true,
  )

  val allLennyFaces: List<CatalogEntry> = listOf(
    // ── CLASSIC & SMIRK ──
    lenny("ln_cl_1", "( ͡° ͜ʖ ͡°)", "Original Lenny Face", LennyCategories.CLASSIC, "classic", "smirk", "meme"),
    lenny("ln_cl_2", "( ͠° ͟ʖ ͡°)", "Skeptical Lenny", LennyCategories.CLASSIC, "raised eyebrow", "doubt"),
    lenny("ln_cl_3", "( ͡° ʖ̯ ͡°)", "Disappointed Lenny", LennyCategories.CLASSIC, "sad", "frown"),
    lenny("ln_cl_4", "( ͡◉ ͜ʖ ͡◉)", "Wide-Eyed Lenny", LennyCategories.CLASSIC, "intense", "stare"),
    lenny("ln_cl_5", "( ͡☉ ͜ʖ ͡☉)", "Shocked Lenny", LennyCategories.CLASSIC, "wide", "whoa"),
    lenny("ln_cl_6", "( ͡° ͜ʖ ͡°)✧", "Sparkle Smirk Lenny", LennyCategories.CLASSIC, "sparkle", "fancy"),
    lenny("ln_cl_7", "( ͡° ͜V ͡°)", "Beak Lenny", LennyCategories.CLASSIC, "bird", "smirk"),
    lenny("ln_cl_8", "[̲̅$̲̅(̲̅ ͡° ͜ʖ ͡°̲̅)̲̅$̲̅]", "Rich Money Lenny", LennyCategories.CLASSIC, "dollar", "cash", "meme"),

    // ── WINKING & PLAYFUL ──
    lenny("ln_wk_1", "( ͡~ ͜ʖ ͡°)", "Winking Lenny", LennyCategories.WINK, "wink", "flirt"),
    lenny("ln_wk_2", "( ͡° ͜ʖ ͡~)", "Right Wink Lenny", LennyCategories.WINK, "wink", "playful"),
    lenny("ln_wk_3", "( ͡^ ͜ʖ ͡^)", "Happy Eyebrow Lenny", LennyCategories.WINK, "smile", "cheer"),
    lenny("ln_wk_4", "( ͡° ͜ʖ ͡°)╭∩╮", "Sassy Lenny", LennyCategories.WINK, "sass", "joke"),
    lenny("ln_wk_5", "(☞ ͡° ͜ʖ ͡°)☞", "Finger Guns Lenny", LennyCategories.WINK, "point", "ayy", "cool"),
    lenny("ln_wk_6", "(͡ ° ͜ʖ ͡ °)", "Spaced Wink Smirk", LennyCategories.WINK, "smirk"),
    lenny("ln_wk_7", "☜(ﾟヮﾟ☜)", "Pointing Meme Left", LennyCategories.WINK, "point", "ayy"),

    // ── SHRUG & MEME ──
    lenny("ln_sh_1", "¯\\_(ツ)_/¯", "Classic Shrug", LennyCategories.SHRUG, "idk", "whatever", "meme"),
    lenny("ln_sh_2", "¯\\_( ͡° ͜ʖ ͡°)_/¯", "Lenny Shrug", LennyCategories.SHRUG, "idk", "smirk"),
    lenny("ln_sh_3", "¯\\(°_o)/¯", "Confused Shrug", LennyCategories.SHRUG, "confused", "dunno"),
    lenny("ln_sh_4", "¯\\_(⊙︿⊙)_/¯", "Worried Shrug", LennyCategories.SHRUG, "oops", "uh"),
    lenny("ln_sh_5", "┐( ͡° ʖ̯ ͡°)┌", "Unimpressed Shrug", LennyCategories.SHRUG, "meh", "shrug"),
    lenny("ln_sh_6", "乁( ◔ ౪◔)「", "Silly Shrug", LennyCategories.SHRUG, "goofy", "meme"),
    lenny("ln_sh_7", "¯\\_( ͠° ͟ʖ °͠ )_/¯", "Suspicious Shrug", LennyCategories.SHRUG, "doubt", "shrug"),

    // ── TABLE FLIP & RAGE ──
    lenny("ln_tf_1", "(ノ ͡° ͜ʖ ͡°)ノ︵┻┻", "Lenny Table Flip", LennyCategories.TABLE_FLIP, "flip", "rage"),
    lenny("ln_tf_2", "(╯°□°)╯︵ ┻━┻", "Classic Table Flip", LennyCategories.TABLE_FLIP, "angry", "flip"),
    lenny("ln_tf_3", "┬─┬ノ( ͡° ͜ʖ ͡°ノ)", "Lenny Respects Tables", LennyCategories.TABLE_FLIP, "polite", "fix"),
    lenny("ln_tf_4", "(ﾉಥ益ಥ）ﾉ ┻━┻", "Rage Flip", LennyCategories.TABLE_FLIP, "mad", "flip"),
    lenny("ln_tf_5", "┻━┻ ︵ ¯\\(ツ)/¯ ︵ ┻━┻", "Double Shrug Flip", LennyCategories.TABLE_FLIP, "chaos", "meme"),
    lenny("ln_tf_6", "(╬ ͡° ͜ʖ ͡°)", "Annoyed Lenny", LennyCategories.TABLE_FLIP, "vein", "mad"),

    // ── FLEX & COOL ──
    lenny("ln_fx_1", "ᕦ( ͡° ͜ʖ ͡°)ᕤ", "Flexing Lenny", LennyCategories.FLEX, "strong", "gym", "muscle"),
    lenny("ln_fx_2", "ᕙ( ͡° ͜ʖ ͡°)ᕗ", "Double Flex Lenny", LennyCategories.FLEX, "power", "buff"),
    lenny("ln_fx_3", "(•_•) / (⌐■_■)", "Deal With It Shades", LennyCategories.FLEX, "sunglasses", "cool"),
    lenny("ln_fx_4", "(⌐■_■)", "Cool Shades Face", LennyCategories.FLEX, "cool", "chill"),
    lenny("ln_fx_5", "(̿▀̿ ̿Ĺ̯̿̿▀̿ ̿)̄", "Boss Shades Lenny", LennyCategories.FLEX, "agent", "boss"),
    lenny("ln_fx_6", "╰( ͡° ͜ʖ ͡° )つ──☆*:・ﾟ", "Wizard Lenny", LennyCategories.FLEX, "magic", "wand"),
    lenny("ln_fx_7", "┌(▀Ĺ̯ ▀-͠ )┐", "Secret Agent", LennyCategories.FLEX, "spy", "cool"),

    // ── CUTE & LOVE ──
    lenny("ln_ct_1", "(♥ ͜ʖ ♥)", "Heart-Eyes Lenny", LennyCategories.CUTE, "love", "heart"),
    lenny("ln_ct_2", "( ͡° ³ ͡°)", "Kissing Lenny", LennyCategories.CUTE, "kiss", "love"),
    lenny("ln_ct_3", "(づ ͡° ͜ʖ ͡°)づ", "Hugging Lenny", LennyCategories.CUTE, "hug", "cuddle"),
    lenny("ln_ct_4", "(｡♥‿♥｡)", "Adoring Face", LennyCategories.CUTE, "crush", "cute"),
    lenny("ln_ct_5", "(っ˘з(˘⌣˘ ) ♡", "Cheek Kiss", LennyCategories.CUTE, "couple", "love"),
    lenny("ln_ct_6", "( ͡° ᴥ ͡°)", "Cute Snout Lenny", LennyCategories.CUTE, "puppy", "cute"),

    // ── QUIRKY & ANIMALS ──
    lenny("ln_qk_1", "ʕ ͡° ͜ʖ ͡°ʔ", "Lenny Bear", LennyCategories.QUIRKY, "bear", "animal"),
    lenny("ln_qk_2", "(= ͡° ͜ʖ ͡°=)", "Lenny Cat", LennyCategories.QUIRKY, "cat", "whiskers"),
    lenny("ln_qk_3", "ಠ_ಠ", "Look of Disapproval", LennyCategories.QUIRKY, "stare", "meme"),
    lenny("ln_qk_4", "(⊙_⊙;)", "Nervous Look", LennyCategories.QUIRKY, "awkward", "sweat"),
    lenny("ln_qk_5", "ヽ༼ຈل͜ຈ༽ﾉ", "Raise Your Dongers", LennyCategories.QUIRKY, "twitch", "meme"),
    lenny("ln_qk_6", "( ͡°( ͡° ͜ʖ( ͡° ͜ʖ ͡°)ʖ ͡°) ͡°)", "Lenny Squad", LennyCategories.QUIRKY, "crowd", "group", "meme"),
  )
}
