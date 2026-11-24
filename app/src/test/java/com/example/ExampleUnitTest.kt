package com.example

import com.example.data.EmojiCatalog
import com.example.data.KaomojiCatalog
import com.example.data.LennyBuilderParts
import com.example.data.LennyCatalog
import com.example.data.SymbolCatalog
import com.example.data.SymbolRepository
import com.example.data.local.FavoriteSymbolEntity
import com.example.data.local.RecentCopyEntity
import com.example.data.local.SavedSnippetEntity
import com.example.data.local.VaultDao
import com.example.model.BioToolsEngine
import com.example.model.CatalogSection
import com.example.model.EmojiCategories
import com.example.model.KaomojiCategories
import com.example.model.LennyCategories
import com.example.model.SymbolCategories
import com.example.model.UnicodeFontEngine
import com.example.model.UnicodeTextUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `all website catalogs have unique ids, unique per-category glyphs, and every category is populated`() {
    val allEntries = SymbolCatalog.allSymbols +
      EmojiCatalog.allEmojis +
      KaomojiCatalog.allKaomoji +
      LennyCatalog.allLennyFaces

    val ids = allEntries.map { it.id }
    assertEquals(ids.size, ids.toSet().size)
    assertTrue(SymbolCatalog.allSymbols.size > 300)
    assertTrue(EmojiCatalog.allEmojis.size > 140)
    assertTrue(KaomojiCatalog.allKaomoji.size > 70)
    assertTrue(LennyCatalog.allLennyFaces.size > 40)

    listOf(
      SymbolCatalog.allSymbols,
      EmojiCatalog.allEmojis,
      KaomojiCatalog.allKaomoji,
      LennyCatalog.allLennyFaces,
    ).forEach { catalog ->
      catalog.groupBy { it.categoryId }.forEach { (catId, entries) ->
        val glyphs = entries.map { it.glyph }
        assertEquals("Duplicate glyph found in category $catId", glyphs.size, glyphs.toSet().size)
      }
    }

    SymbolCategories.all.filter { it.id != "all" && it.id != "favorites" }.forEach { cat ->
      assertTrue("Missing symbols for ${cat.label}", SymbolCatalog.allSymbols.any { it.categoryId == cat.id })
    }
    EmojiCategories.all.filter { it.id != "all" && it.id != "favorites" }.forEach { cat ->
      assertTrue("Missing emojis for ${cat.label}", EmojiCatalog.allEmojis.any { it.categoryId == cat.id })
    }
    KaomojiCategories.all.filter { it.id != "all" && it.id != "favorites" }.forEach { cat ->
      assertTrue("Missing kaomoji for ${cat.label}", KaomojiCatalog.allKaomoji.any { it.categoryId == cat.id })
    }
    LennyCategories.all.filter { it.id != "all" && it.id != "favorites" }.forEach { cat ->
      assertTrue("Missing lenny for ${cat.label}", LennyCatalog.allLennyFaces.any { it.categoryId == cat.id })
    }
  }

  @Test
  fun `unicode font engine transforms styles and preserves surrogate pairs cleanly`() {
    val boldSans = UnicodeFontEngine.styles.first { it.id == "bold_sans" }
    assertEquals("𝗦𝘆𝗺𝗯𝗼𝗹", boldSans.transform("Symbol"))

    val smallCaps = UnicodeFontEngine.styles.first { it.id == "small_caps" }
    assertEquals("ʜᴇʟʟᴏ", smallCaps.transform("Hello"))

    val scriptLight = UnicodeFontEngine.styles.first { it.id == "script_light" }
    assertEquals("ℬℯ", scriptLight.transform("Be"))

    // 'B' maps to U+10412 (surrogate pair 𐐒); reversing must not corrupt surrogate pairs
    val upsideDown = UnicodeFontEngine.styles.first { it.id == "upside_down" }
    assertEquals("∀𐐒", upsideDown.transform("BA"))
    assertEquals("🔥∀", upsideDown.transform("A🔥"))

    // Combining marks must not split surrogate pairs
    val strike = UnicodeFontEngine.styles.first { it.id == "strikethrough" }
    val struckEmoji = strike.transform("🔥")
    assertEquals("🔥\u0336", struckEmoji)
  }

  @Test
  fun `grapheme reversal, spacing, backspace, and invisible text helpers work safely`() {
    assertEquals("A B 🔥", BioToolsEngine.toSpacedAesthetic("AB🔥"))
    assertEquals("🔥BA", BioToolsEngine.toReversedText("AB🔥"))
    assertEquals("Hi ", UnicodeTextUtils.removeLastGrapheme("Hi ❤️‍🔥"))
    assertEquals("Hi ", UnicodeTextUtils.removeLastGrapheme("Hi 🏳️‍🌈"))
    assertEquals("🇺🇸", UnicodeTextUtils.removeLastGrapheme("🇺🇸🇯🇵"))

    assertTrue(UnicodeTextUtils.isInvisibleText("\u3164"))
    assertTrue(UnicodeTextUtils.isInvisibleText("\u2800\n\u2800"))
    assertTrue(UnicodeTextUtils.isInvisibleText("\u200B"))
    assertFalse(UnicodeTextUtils.isInvisibleText("✦"))
    assertTrue(UnicodeTextUtils.formatVisiblePreview("\u2800\n\u2800").contains("2 blank lines"))
  }

  @Test
  fun `lenny builder and bio tools engine work accurately`() {
    val built = LennyBuilderParts.build(0, 0, 0, 0)
    assertTrue(built.contains(" ͡°"))

    val morse = BioToolsEngine.toMorse("SOS")
    assertEquals("... --- ...", morse)
    assertEquals("SOS", BioToolsEngine.fromMorse(morse))
    assertEquals(".... .. / - .... . .-. .", BioToolsEngine.toMorse("Hi @@@ There"))
    assertEquals("HI THERE", BioToolsEngine.fromMorse(".... .. / ??? / - .... . .-. ."))
    assertEquals("꧁༒ Nova ༒꧂", BioToolsEngine.frames.first().wrap("Nova"))
    assertEquals("\u2800\n\u2800\n\u2800", BioToolsEngine.makeInstagramSpacerBlock(3))
  }

  @Test
  fun `favorites repository toggles, tracks usage frequency, and filters favorites category`() = runBlocking {
    val fakeDao = InMemoryVaultDao()
    val repo = SymbolRepository(fakeDao)

    val addedStar = repo.toggleFavorite("✧", "White Four Pointed Star", "Symbols · Stars")
    val addedHeart = repo.toggleFavorite("♥", "Black Heart Suit", "Symbols · Hearts")
    assertTrue(addedStar)
    assertTrue(addedHeart)

    // Copying a favorited symbol increments its useCount and ranks it first in Most Used
    repo.recordCopy("♥", "Black Heart Suit")
    repo.recordCopy("♥", "Black Heart Suit")
    repo.recordCopy("✧", "White Four Pointed Star")

    val mostUsed = repo.observeFavoritesByMostUsed().first()
    assertEquals(2, mostUsed.size)
    assertEquals("♥", mostUsed.first().glyph)
    assertEquals(2, mostUsed.first().useCount)
    assertEquals("✧", mostUsed[1].glyph)
    assertEquals(1, mostUsed[1].useCount)

    // Filtering by virtual "favorites" category returns only favorited glyphs
    val favSet = mostUsed.map { it.glyph }.toSet()
    val filteredFavSymbols = repo.filterEntries(
      section = CatalogSection.SYMBOLS,
      query = "",
      categoryId = "favorites",
      favoriteGlyphs = favSet,
    )
    assertTrue(filteredFavSymbols.isNotEmpty())
    assertTrue(filteredFavSymbols.all { it.glyph in favSet })

    // Toggling an existing favorite removes it
    val stillAdded = repo.toggleFavorite("✧", "White Four Pointed Star", "Symbols · Stars")
    assertFalse(stillAdded)
    assertEquals(1, repo.observeFavorites().first().size)
  }

  private class InMemoryVaultDao : VaultDao {
    private val favorites = MutableStateFlow<List<FavoriteSymbolEntity>>(emptyList())
    private val recents = MutableStateFlow<List<RecentCopyEntity>>(emptyList())
    private val snippets = MutableStateFlow<List<SavedSnippetEntity>>(emptyList())
    private var nextId = 1L

    override fun observeFavorites(): Flow<List<FavoriteSymbolEntity>> =
      favorites.map { list -> list.sortedByDescending { it.addedAt } }

    override fun observeFavoritesByMostUsed(): Flow<List<FavoriteSymbolEntity>> =
      favorites.map { list ->
        list.sortedWith(
          compareByDescending<FavoriteSymbolEntity> { it.useCount }
            .thenByDescending { it.lastUsedAt }
            .thenByDescending { it.addedAt }
        )
      }

    override suspend fun upsertFavorite(entity: FavoriteSymbolEntity) {
      favorites.value = favorites.value.filterNot { it.glyph == entity.glyph } + entity
    }

    override suspend fun incrementFavoriteUsage(glyph: String, usedAt: Long) {
      favorites.value = favorites.value.map { fav ->
        if (fav.glyph == glyph) fav.copy(useCount = fav.useCount + 1, lastUsedAt = usedAt) else fav
      }
    }

    override suspend fun removeFavorite(glyph: String) {
      favorites.value = favorites.value.filterNot { it.glyph == glyph }
    }

    override suspend fun clearFavorites() {
      favorites.value = emptyList()
    }

    override suspend fun isFavorite(glyph: String): Boolean =
      favorites.value.any { it.glyph == glyph }

    override fun observeRecentCopies(): Flow<List<RecentCopyEntity>> = recents

    override suspend fun insertRecent(entity: RecentCopyEntity) {
      recents.value = listOf(entity.copy(id = nextId++)) + recents.value
    }

    override suspend fun deleteRecentByText(text: String) {
      recents.value = recents.value.filterNot { it.text == text }
    }

    override suspend fun trimRecentCopies() {
      recents.value = recents.value.take(50)
    }

    override suspend fun clearRecentCopies() {
      recents.value = emptyList()
    }

    override fun observeSnippets(): Flow<List<SavedSnippetEntity>> = snippets

    override suspend fun insertSnippet(entity: SavedSnippetEntity) {
      val id = nextId++
      snippets.value = listOf(entity.copy(id = id)) + snippets.value
    }

    override suspend fun deleteSnippet(id: Long) {
      snippets.value = snippets.value.filterNot { it.id == id }
    }
  }
}
