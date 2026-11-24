package com.example.data

import com.example.model.CatalogCategory
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import com.example.model.KaomojiCategories

object KaomojiCatalog {

  private fun kao(
    id: String,
    glyph: String,
    name: String,
    category: CatalogCategory,
    vararg tags: String,
  ) = CatalogEntry(
    id = id,
    glyph = glyph,
    name = name,
    section = CatalogSection.KAOMOJI,
    categoryId = category.id,
    categoryLabel = category.label,
    tags = (tags.toList() + name.lowercase().split(" ") + category.label.lowercase().split(" ")).distinct(),
    isWide = true,
  )

  val allKaomoji: List<CatalogEntry> = listOf(
    // ── HAPPY & JOY ──
    kao("km_hp_1", "٩(ˊᗜˋ*)و", "Cheering Excited", KaomojiCategories.HAPPY, "yay", "win", "celebrate"),
    kao("km_hp_2", "(o^▽^o)", "Joyful Smile", KaomojiCategories.HAPPY, "smile", "glad"),
    kao("km_hp_3", "(＾▽＾)", "Classic Bright Smile", KaomojiCategories.HAPPY, "smile", "cheerful"),
    kao("km_hp_4", "(˶ᵔ ᵕ ᵔ˶)", "Soft Warm Smile", KaomojiCategories.HAPPY, "cozy", "cute"),
    kao("km_hp_5", "＼(＾▽＾)／", "Arms Up Celebration", KaomojiCategories.HAPPY, "party", "hooray"),
    kao("km_hp_6", "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧", "Throwing Sparkles", KaomojiCategories.HAPPY, "magic", "sparkle"),
    kao("km_hp_7", "(*^▽^*)", "Blushing Happy", KaomojiCategories.HAPPY, "blush", "glad"),
    kao("km_hp_8", "ヽ(>∀<☆)ノ", "Star Jump Joy", KaomojiCategories.HAPPY, "star", "excited"),
    kao("km_hp_9", "(≧◡≦)", "Delighted Grin", KaomojiCategories.HAPPY, "cute", "grin"),
    kao("km_hp_10", "♪(´ε｀ )", "Whistling Happy", KaomojiCategories.HAPPY, "music", "chill"),

    // ── LOVE & CUTE ──
    kao("km_lv_1", "(*♡∀♡)", "Heart Eyes Adore", KaomojiCategories.LOVE, "crush", "hearts"),
    kao("km_lv_2", "( ˘ ³˘)♥", "Blowing a Kiss", KaomojiCategories.LOVE, "kiss", "romance"),
    kao("km_lv_3", "(♡˙︶˙♡)", "Dreamy Love", KaomojiCategories.LOVE, "cute", "blush"),
    kao("km_lv_4", "(´∀｀)♡", "Sweet Affection", KaomojiCategories.LOVE, "heart", "warm"),
    kao("km_lv_5", "(｡♥‿♥｡)", "Lovestruck", KaomojiCategories.LOVE, "eyes", "crush"),
    kao("km_lv_6", "(灬º‿º灬)♡", "Shy Blushing Love", KaomojiCategories.LOVE, "blush", "shy"),
    kao("km_lv_7", "(*˘︶˘*).｡.:*♡", "Floating Heart Wish", KaomojiCategories.LOVE, "sparkle", "sweet"),
    kao("km_lv_8", "(´,,•ω•,,)♡", "Flustered Cute", KaomojiCategories.LOVE, "kawaii", "blush"),
    kao("km_lv_9", "♡( ◡‿◡ )", "Peaceful Love", KaomojiCategories.LOVE, "calm", "heart"),
    kao("km_lv_10", "(づ￣ ³￣)づ", "Kissing Hug", KaomojiCategories.LOVE, "hug", "kiss"),

    // ── SAD & CRYING ──
    kao("km_sd_1", "(╥﹏╥)", "Crying Streams", KaomojiCategories.SAD, "tears", "sob"),
    kao("km_sd_2", "(ノ_<｡)", "Shy Sobbing", KaomojiCategories.SAD, "upset", "hurt"),
    kao("km_sd_3", "(っ˘̩╭╮˘̩)っ", "Comforting Sad Hug", KaomojiCategories.SAD, "hug", "miss"),
    kao("km_sd_4", "(；′⌒`)", "Teary Pout", KaomojiCategories.SAD, "sad", "pout"),
    kao("km_sd_5", "(｡•́︿•̀｡)", "Downcast Eyes", KaomojiCategories.SAD, "sorry", "gloomy"),
    kao("km_sd_6", "(T_T)", "Classic Tears", KaomojiCategories.SAD, "cry", "minimal"),
    kao("km_sd_7", "(｡╯︵╰｡)", "Heartbroken", KaomojiCategories.SAD, "disappointed"),
    kao("km_sd_8", "(ಥ﹏ಥ)", "Dramatic Crying", KaomojiCategories.SAD, "emotional", "tears"),
    kao("km_sd_9", "(´；ω；`)", "Sniffling Cute", KaomojiCategories.SAD, "sad", "please"),
    kao("km_sd_10", "(个_个)", "Quiet Tears", KaomojiCategories.SAD, "cry"),

    // ── ANGRY & RAGE ──
    kao("km_ag_1", "(╬ಠ益ಠ)", "Furious Rage", KaomojiCategories.ANGRY, "mad", "vein"),
    kao("km_ag_2", "(ᓄಠ_ಠ)ᓄ", "Ready to Fight", KaomojiCategories.ANGRY, "battle", "fists"),
    kao("km_ag_3", "(╯°□°）╯︵ ┻━┻", "Classic Table Flip", KaomojiCategories.ANGRY, "rage", "flip", "meme"),
    kao("km_ag_4", "(ง'̀-'́)ง", "Boxing Stance", KaomojiCategories.ANGRY, "fight", "punch"),
    kao("km_ag_5", "(눈_눈)", "Cold Glare", KaomojiCategories.ANGRY, "annoyed", "stare"),
    kao("km_ag_6", "(；¬д¬)", "Suspicious Side-Eye", KaomojiCategories.ANGRY, "doubt", "annoyed"),
    kao("km_ag_7", "(`皿´＃)", "Snapping Mad", KaomojiCategories.ANGRY, "grr", "yell"),
    kao("km_ag_8", "┻━┻ ︵ヽ(`Д´)ﾉ︵ ┻━┻", "Double Table Flip", KaomojiCategories.ANGRY, "chaos", "rage"),
    kao("km_ag_9", "(ಠ_ಠ)", "Disapproving Stare", KaomojiCategories.ANGRY, "judging", "serious"),
    kao("km_ag_10", "ψ(｀∇´)ψ", "Evil Plotting", KaomojiCategories.ANGRY, "mischief", "villain"),

    // ── SURPRISED & SHY ──
    kao("km_sp_1", "(⊙_⊙)", "Wide-Eyed Shock", KaomojiCategories.SURPRISED, "what", "whoa"),
    kao("km_sp_2", "(°ロ°)", "Jaw Drop", KaomojiCategories.SURPRISED, "shock", "omg"),
    kao("km_sp_3", "(・_・;)", "Awkward Sweat", KaomojiCategories.SURPRISED, "nervous", "uh"),
    kao("km_sp_4", "(⁄ ⁄•⁄ω⁄•⁄ ⁄)", "Super Shy Blush", KaomojiCategories.SURPRISED, "embarrassed", "cute"),
    kao("km_sp_5", "(⊙_☉)", "Confused Blink", KaomojiCategories.SURPRISED, "huh", "confused"),
    kao("km_sp_6", "(◎_◎;)", "Startled Freeze", KaomojiCategories.SURPRISED, "panic", "sweat"),
    kao("km_sp_7", "(*/ω＼*)", "Hiding Face Shyly", KaomojiCategories.SURPRISED, "peek", "blush"),
    kao("km_sp_8", "(・∧‐)ゞ", "Playful Salute", KaomojiCategories.SURPRISED, "oops", "wink"),

    // ── HUGS & ACTIONS ──
    kao("km_ac_1", "(づ｡◕‿‿◕｡)づ", "Warm Open Hug", KaomojiCategories.ACTIONS, "hug", "cuddle"),
    kao("km_ac_2", "┬─┬ノ( º _ ºノ)", "Put Table Back", KaomojiCategories.ACTIONS, "polite", "calm", "meme"),
    kao("km_ac_3", "(ง •̀_•́)ง", "Determined Fist", KaomojiCategories.ACTIONS, "motivated", "go"),
    kao("km_ac_4", "ε=ε=ε=┌(;*´Д`)ﾉ", "Running Fast", KaomojiCategories.ACTIONS, "run", "late"),
    kao("km_ac_5", "(っ´▽`)っ", "Friendly Reach Hug", KaomojiCategories.ACTIONS, "hug", "welcome"),
    kao("km_ac_6", "ヾ(・ω・*)", "Gentle Wave Bye", KaomojiCategories.ACTIONS, "wave", "hi", "bye"),
    kao("km_ac_7", "(つ✧ω✧)つ", "Sparkle Hug", KaomojiCategories.ACTIONS, "hug", "excited"),
    kao("km_ac_8", "┏(＾0＾)┛", "Happy Dance", KaomojiCategories.ACTIONS, "dance", "vibe"),
    kao("km_ac_9", "(－_－) zzZ", "Sleeping Zzz", KaomojiCategories.ACTIONS, "sleep", "tired"),
    kao("km_ac_10", "(*•̀ᴗ•́*)و ̑̑", "You Got This!", KaomojiCategories.ACTIONS, "cheer", "support"),

    // ── ANIMALS & CATS ──
    kao("km_an_1", "=＾● ⋏ ●＾=", "Cute Kitty Cat", KaomojiCategories.ANIMALS, "cat", "meow"),
    kao("km_an_2", "(=^･ω･^=)", "Classic Cat Face", KaomojiCategories.ANIMALS, "cat", "neko"),
    kao("km_an_3", "ʕ•ᴥ•ʔ", "Little Bear", KaomojiCategories.ANIMALS, "bear", "cute"),
    kao("km_an_4", "ʕっ•ᴥ•ʔっ", "Bear Hug", KaomojiCategories.ANIMALS, "bear", "hug"),
    kao("km_an_5", "(\\_/)\n( •_•)\n/ >♥", "Bunny Holding Heart", KaomojiCategories.ANIMALS, "rabbit", "love"),
    kao("km_an_6", "U・ᴥ・U", "Sweet Puppy", KaomojiCategories.ANIMALS, "dog", "woof"),
    kao("km_an_7", "(・⊝・)", "Tiny Birdie", KaomojiCategories.ANIMALS, "bird", "chick"),
    kao("km_an_8", "ᘛ⁐̤ᕐᐷ", "Little Mouse", KaomojiCategories.ANIMALS, "mouse", "tiny"),
    kao("km_an_9", "(=^-ω-^=)", "Sleepy Cat", KaomojiCategories.ANIMALS, "cat", "nap"),
    kao("km_an_10", "▼・ᴥ・▼", "Fluffy Doggo", KaomojiCategories.ANIMALS, "dog", "pet"),

    // ── SPARKLE & COQUETTE ──
    kao("km_sk_1", "✧( ु•⌄• )◞", "Sparkle Greeting", KaomojiCategories.SPARKLE, "aesthetic", "hi"),
    kao("km_sk_2", "₊˚⊹ ᰔ (˶ˆᗜˆ˵)", "Coquette Heart Smile", KaomojiCategories.SPARKLE, "cute", "bio"),
    kao("km_sk_3", "⋆｡°✩(｡•̀ᴗ-)✧", "Starry Wink", KaomojiCategories.SPARKLE, "wink", "star"),
    kao("km_sk_4", "(ﾉ´ヮ`)ﾉ*: ･ﾟ", "Fairy Dust Toss", KaomojiCategories.SPARKLE, "magic", "cute"),
    kao("km_sk_5", "꒰ঌ( •ө• )໒꒱", "Angel Wings Bird", KaomojiCategories.SPARKLE, "wings", "angel"),
    kao("km_sk_6", "♡( ˃͈ ˂͈ )♡", "Squeezed With Joy", KaomojiCategories.SPARKLE, "kawaii", "heart"),
  )
}
