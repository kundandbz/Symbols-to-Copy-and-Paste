package com.example.model

enum class CatalogSection(val label: String) {
  SYMBOLS("Symbols"),
  EMOJIS("Emojis"),
  KAOMOJI("Kaomoji"),
  LENNY("Lenny Faces"),
}

data class CatalogCategory(
  val id: String,
  val label: String,
  val badge: String,
)

data class CatalogEntry(
  val id: String,
  val glyph: String,
  val name: String,
  val section: CatalogSection,
  val categoryId: String,
  val categoryLabel: String,
  val tags: List<String>,
  val isWide: Boolean = false,
) {
  val codepointSummary: String
    get() {
      val cps = mutableListOf<String>()
      var i = 0
      while (i < glyph.length) {
        val cp = glyph.codePointAt(i)
        cps.add("U+%04X".format(cp))
        i += Character.charCount(cp)
      }
      return if (cps.size <= 3) cps.joinToString(" ")
      else "${cps.take(2).joinToString(" ")} … (${cps.size})"
    }
}

object SymbolCategories {
  val ALL = CatalogCategory("all", "All Symbols", "✦")
  val FAVORITES = CatalogCategory("favorites", "Favorites", "♥")
  val HEARTS = CatalogCategory("hearts", "Hearts", "♡")
  val STARS = CatalogCategory("stars", "Stars & Sparkles", "★")
  val ARROWS = CatalogCategory("arrows", "Arrows", "➤")
  val AESTHETIC = CatalogCategory("aesthetic", "Aesthetic & Borders", "✧")
  val GAMER = CatalogCategory("gamer", "Gamer & Clan", "亗")
  val CHECKMARKS = CatalogCategory("checkmarks", "Checkmarks", "✓")
  val FLOWERS = CatalogCategory("flowers", "Flowers & Nature", "✿")
  val CURRENCY = CatalogCategory("currency", "Currency", "$")
  val MATH = CatalogCategory("math", "Math & Science", "∞")
  val FRACTIONS = CatalogCategory("fractions", "Fractions & Numbers", "½")
  val BRACKETS = CatalogCategory("brackets", "Brackets & Quotes", "【】")
  val PUNCTUATION = CatalogCategory("punctuation", "Punctuation", "•")
  val MUSIC = CatalogCategory("music", "Music", "♫")
  val CHESS_CARDS = CatalogCategory("chess_cards", "Chess & Cards", "♛")
  val ZODIAC = CatalogCategory("zodiac", "Zodiac & Astrology", "☽")
  val SHAPES = CatalogCategory("shapes", "Geometric Shapes", "◆")
  val BOX_LINES = CatalogCategory("box_lines", "Lines & Box", "═")
  val TECHNICAL = CatalogCategory("technical", "Keyboard & UI", "⌘")
  val GREEK = CatalogCategory("greek", "Greek Letters", "Ω")
  val CULTURAL = CatalogCategory("cultural", "Peace & Culture", "☯")

  val all: List<CatalogCategory> = listOf(
    ALL, FAVORITES, HEARTS, STARS, ARROWS, AESTHETIC, GAMER, CHECKMARKS, FLOWERS,
    CURRENCY, MATH, FRACTIONS, BRACKETS, PUNCTUATION, MUSIC, CHESS_CARDS,
    ZODIAC, SHAPES, BOX_LINES, TECHNICAL, GREEK, CULTURAL,
  )
}

object EmojiCategories {
  val ALL = CatalogCategory("all", "All Emojis", "✨")
  val FAVORITES = CatalogCategory("favorites", "Favorites", "❤️")
  val SMILEYS = CatalogCategory("smileys", "Smileys & Emotion", "😊")
  val HEARTS = CatalogCategory("emoji_hearts", "Hearts & Love", "💖")
  val GESTURES = CatalogCategory("gestures", "Hands & Gestures", "🫶")
  val ANIMALS = CatalogCategory("animals", "Animals & Nature", "🦋")
  val FOOD = CatalogCategory("food", "Food & Drink", "🍓")
  val TRAVEL = CatalogCategory("travel", "Travel & Weather", "🌙")
  val ACTIVITIES = CatalogCategory("activities", "Objects & Vibes", "🎧")
  val FLAGS = CatalogCategory("flags", "Flags & Badges", "🏁")

  val all: List<CatalogCategory> = listOf(
    ALL, FAVORITES, SMILEYS, HEARTS, GESTURES, ANIMALS, FOOD, TRAVEL, ACTIVITIES, FLAGS,
  )
}

object KaomojiCategories {
  val ALL = CatalogCategory("all", "All Kaomoji", "٩(◕‿◕)۶")
  val FAVORITES = CatalogCategory("favorites", "Favorites", "♡")
  val HAPPY = CatalogCategory("happy", "Happy & Joy", "(^▽^)")
  val LOVE = CatalogCategory("love", "Love & Cute", "(♡˙︶˙♡)")
  val SAD = CatalogCategory("sad", "Sad & Crying", "(╥﹏╥)")
  val ANGRY = CatalogCategory("angry", "Angry & Rage", "(╬ಠ益ಠ)")
  val SURPRISED = CatalogCategory("surprised", "Surprised & Shy", "(⊙_⊙)")
  val ACTIONS = CatalogCategory("actions", "Hugs & Actions", "(づ｡◕‿‿◕｡)づ")
  val ANIMALS = CatalogCategory("k_animals", "Animals & Cats", "ʕ•ᴥ•ʔ")
  val SPARKLE = CatalogCategory("k_sparkle", "Sparkle & Coquette", "✧˖°")

  val all: List<CatalogCategory> = listOf(
    ALL, FAVORITES, HAPPY, LOVE, SAD, ANGRY, SURPRISED, ACTIONS, ANIMALS, SPARKLE,
  )
}

object LennyCategories {
  val ALL = CatalogCategory("all", "All Lenny", "( ͡° ͜ʖ ͡°)")
  val FAVORITES = CatalogCategory("favorites", "Favorites", "♥")
  val CLASSIC = CatalogCategory("classic", "Classic & Smirk", " ͡°")
  val WINK = CatalogCategory("wink", "Winking & Flirty", " ͡~")
  val SHRUG = CatalogCategory("shrug", "Shrug & Meme", "¯\\_(ツ)_/¯")
  val TABLE_FLIP = CatalogCategory("table_flip", "Table Flip", "┻━┻")
  val FLEX = CatalogCategory("flex", "Flex & Cool", "ᕦ(ツ)ᕤ")
  val CUTE = CatalogCategory("lenny_cute", "Cute & Love", "♥")
  val QUIRKY = CatalogCategory("quirky", "Quirky & Animals", "ʕ•ᴥ•ʔ")

  val all: List<CatalogCategory> = listOf(
    ALL, FAVORITES, CLASSIC, WINK, SHRUG, TABLE_FLIP, FLEX, CUTE, QUIRKY,
  )
}
