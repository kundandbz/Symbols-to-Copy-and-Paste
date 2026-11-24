package com.example

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.EmojiCatalog
import com.example.data.KaomojiCatalog
import com.example.data.LennyBuilderParts
import com.example.data.LennyCatalog
import com.example.data.SymbolCatalog
import com.example.data.SymbolRepository
import com.example.data.local.SymbolPadDatabase
import com.example.model.BioToolsEngine
import com.example.model.CatalogSection
import com.example.model.EmojiCategories
import com.example.model.KaomojiCategories
import com.example.model.LennyCategories
import com.example.model.SymbolCategories
import com.example.model.UnicodeFontEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun `app name matches Symbols to Copy and Paste`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Symbols to Copy & Paste", appName)
  }

  @Test
  fun `all website catalogs have unique ids and populated categories`() {
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

    // Verify every non-All/non-Favorites category has items
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
  fun `unicode font engine and lenny builder work cleanly`() {
    val boldSans = UnicodeFontEngine.styles.first { it.id == "bold_sans" }
    assertEquals("𝗦𝘆𝗺𝗯𝗼𝗹", boldSans.transform("Symbol"))

    val smallCaps = UnicodeFontEngine.styles.first { it.id == "small_caps" }
    assertEquals("ʜᴇʟʟᴏ", smallCaps.transform("Hello"))

    val built = LennyBuilderParts.build(0, 0, 0, 0)
    assertTrue(built.contains(" ͡°"))
  }

  @Test
  fun `bio tools engine handles morse and frames`() {
    val morse = BioToolsEngine.toMorse("SOS")
    assertEquals("... --- ...", morse)
    assertEquals("SOS", BioToolsEngine.fromMorse(morse))
    assertEquals("꧁༒ Nova ༒꧂", BioToolsEngine.frames.first().wrap("Nova"))
  }

  @Test
  fun `repository filters each section cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, SymbolPadDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    val repo = SymbolRepository(db.vaultDao())

    val stars = repo.filterEntries(CatalogSection.SYMBOLS, "star", SymbolCategories.STARS.id)
    assertTrue(stars.isNotEmpty())
    assertTrue(stars.all { it.categoryId == SymbolCategories.STARS.id })

    val cats = repo.filterEntries(CatalogSection.KAOMOJI, "cat", "all")
    assertTrue(cats.isNotEmpty())

    val lennyWink = repo.filterEntries(CatalogSection.LENNY, "", LennyCategories.WINK.id)
    assertTrue(lennyWink.isNotEmpty())
    db.close()
  }

  @Test
  fun `symbols search and tap copies glyph to clipboard`() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("symbol_search_input").performTextInput("White Four Pointed")
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("catalog_tile_sym_st_1").performClick()
    composeTestRule.waitForIdle()

    val context = ApplicationProvider.getApplicationContext<Context>()
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    assertEquals("✧", clipboard.primaryClip?.getItemAt(0)?.text?.toString())
    composeTestRule.onNodeWithTag("floating_copy_banner").assertIsDisplayed()
  }

  @Test
  fun `navigate across all 5 website tabs and vault`() {
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("nav_emojis").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("emoji_search_input").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_kaomoji").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("kaomoji_search_input").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_lenny").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("lenny_builder_preview").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_tools").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("fonts_text_input").assertIsDisplayed()

    composeTestRule.onNodeWithTag("nav_vault").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("vault_tab_0").assertIsDisplayed()
  }
}
