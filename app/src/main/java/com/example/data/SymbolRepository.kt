package com.example.data

import com.example.data.local.FavoriteSymbolEntity
import com.example.data.local.SavedSnippetEntity
import com.example.data.local.VaultDao
import com.example.model.CatalogCategory
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import com.example.model.EmojiCategories
import com.example.model.KaomojiCategories
import com.example.model.LennyCategories
import com.example.model.SymbolCategories
import kotlinx.coroutines.flow.Flow

class SymbolRepository(
  private val vaultDao: VaultDao,
) {
  val allSymbols: List<CatalogEntry> by lazy(LazyThreadSafetyMode.NONE) { SymbolCatalog.allSymbols }
  val trendingSymbols: List<CatalogEntry> by lazy(LazyThreadSafetyMode.NONE) { SymbolCatalog.trendingSymbols }
  val allEmojis: List<CatalogEntry> by lazy(LazyThreadSafetyMode.NONE) { EmojiCatalog.allEmojis }
  val allKaomoji: List<CatalogEntry> by lazy(LazyThreadSafetyMode.NONE) { KaomojiCatalog.allKaomoji }
  val allLennyFaces: List<CatalogEntry> by lazy(LazyThreadSafetyMode.NONE) { LennyCatalog.allLennyFaces }

  private val symbolsByCategory: Map<String, List<CatalogEntry>> by lazy(LazyThreadSafetyMode.NONE) {
    allSymbols.groupBy { it.categoryId }
  }
  private val emojisByCategory: Map<String, List<CatalogEntry>> by lazy(LazyThreadSafetyMode.NONE) {
    allEmojis.groupBy { it.categoryId }
  }
  private val kaomojiByCategory: Map<String, List<CatalogEntry>> by lazy(LazyThreadSafetyMode.NONE) {
    allKaomoji.groupBy { it.categoryId }
  }
  private val lennyByCategory: Map<String, List<CatalogEntry>> by lazy(LazyThreadSafetyMode.NONE) {
    allLennyFaces.groupBy { it.categoryId }
  }

  val symbolCategoryCounts: Map<String, Int> by lazy(LazyThreadSafetyMode.NONE) {
    symbolsByCategory.mapValues { it.value.size }
  }

  val groupedSymbols: List<Pair<CatalogCategory, List<CatalogEntry>>> by lazy(LazyThreadSafetyMode.NONE) {
    buildGroupedList(SymbolCategories.all, symbolsByCategory)
  }
  val groupedEmojis: List<Pair<CatalogCategory, List<CatalogEntry>>> by lazy(LazyThreadSafetyMode.NONE) {
    buildGroupedList(EmojiCategories.all, emojisByCategory)
  }
  val groupedKaomoji: List<Pair<CatalogCategory, List<CatalogEntry>>> by lazy(LazyThreadSafetyMode.NONE) {
    buildGroupedList(KaomojiCategories.all, kaomojiByCategory)
  }
  val groupedLenny: List<Pair<CatalogCategory, List<CatalogEntry>>> by lazy(LazyThreadSafetyMode.NONE) {
    buildGroupedList(LennyCategories.all, lennyByCategory)
  }

  private fun buildGroupedList(
    categories: List<CatalogCategory>,
    byCategory: Map<String, List<CatalogEntry>>,
  ): List<Pair<CatalogCategory, List<CatalogEntry>>> =
    categories
      .asSequence()
      .filter { it.id != "all" && it.id != "favorites" }
      .mapNotNull { cat ->
        val items = byCategory[cat.id]
        if (items.isNullOrEmpty()) null else cat to items
      }
      .toList()

  fun entriesForSection(section: CatalogSection): List<CatalogEntry> = when (section) {
    CatalogSection.SYMBOLS -> allSymbols
    CatalogSection.EMOJIS -> allEmojis
    CatalogSection.KAOMOJI -> allKaomoji
    CatalogSection.LENNY -> allLennyFaces
  }

  fun groupedForSection(section: CatalogSection): List<Pair<CatalogCategory, List<CatalogEntry>>> = when (section) {
    CatalogSection.SYMBOLS -> groupedSymbols
    CatalogSection.EMOJIS -> groupedEmojis
    CatalogSection.KAOMOJI -> groupedKaomoji
    CatalogSection.LENNY -> groupedLenny
  }

  private fun categoryMapForSection(section: CatalogSection): Map<String, List<CatalogEntry>> = when (section) {
    CatalogSection.SYMBOLS -> symbolsByCategory
    CatalogSection.EMOJIS -> emojisByCategory
    CatalogSection.KAOMOJI -> kaomojiByCategory
    CatalogSection.LENNY -> lennyByCategory
  }

  fun filterEntries(
    section: CatalogSection,
    query: String,
    categoryId: String,
    favoriteGlyphs: Set<String> = emptySet(),
  ): List<CatalogEntry> {
    val base = when (categoryId) {
      "all" -> entriesForSection(section)
      "favorites" -> {
        if (favoriteGlyphs.isEmpty()) return emptyList()
        entriesForSection(section).filter { favoriteGlyphs.contains(it.glyph) }
      }
      else -> categoryMapForSection(section)[categoryId].orEmpty()
    }
    val q = query.trim().lowercase()
    if (q.isEmpty()) return base
    return base.filter { item -> item.searchIndex.contains(q) }
  }

  fun observeFavorites(): Flow<List<FavoriteSymbolEntity>> = vaultDao.observeFavorites()

  fun observeFavoritesByMostUsed(): Flow<List<FavoriteSymbolEntity>> = vaultDao.observeFavoritesByMostUsed()

  fun observeRecentCopies() = vaultDao.observeRecentCopies()

  fun observeSnippets(): Flow<List<SavedSnippetEntity>> = vaultDao.observeSnippets()

  suspend fun toggleFavorite(glyph: String, label: String, categoryName: String): Boolean {
    return if (vaultDao.isFavorite(glyph)) {
      vaultDao.removeFavorite(glyph)
      false
    } else {
      vaultDao.upsertFavorite(
        FavoriteSymbolEntity(
          glyph = glyph,
          label = label,
          categoryName = categoryName,
        )
      )
      true
    }
  }

  suspend fun removeFavorite(glyph: String) {
    vaultDao.removeFavorite(glyph)
  }

  suspend fun recordCopy(text: String, sourceLabel: String) {
    if (text.isEmpty()) return
    vaultDao.recordCopyTransaction(text = text, sourceLabel = sourceLabel)
  }

  suspend fun clearRecentCopies() {
    vaultDao.clearRecentCopies()
  }

  suspend fun saveSnippet(title: String, content: String, kind: String) {
    if (content.isEmpty()) return
    vaultDao.insertSnippet(
      SavedSnippetEntity(
        title = title.ifBlank { "Custom Combo" },
        content = content,
        kind = kind,
      )
    )
  }

  suspend fun deleteSnippet(id: Long) {
    vaultDao.deleteSnippet(id)
  }
}
