package com.example.data

import com.example.data.local.FavoriteSymbolEntity
import com.example.data.local.RecentCopyEntity
import com.example.data.local.SavedSnippetEntity
import com.example.data.local.VaultDao
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import kotlinx.coroutines.flow.Flow

class SymbolRepository(
  private val vaultDao: VaultDao,
) {
  val allSymbols: List<CatalogEntry> = SymbolCatalog.allSymbols
  val trendingSymbols: List<CatalogEntry> = SymbolCatalog.trendingSymbols
  val symbolCategoryCounts: Map<String, Int> = allSymbols.groupingBy { it.categoryId }.eachCount()
  val allEmojis: List<CatalogEntry> = EmojiCatalog.allEmojis
  val allKaomoji: List<CatalogEntry> = KaomojiCatalog.allKaomoji
  val allLennyFaces: List<CatalogEntry> = LennyCatalog.allLennyFaces

  fun entriesForSection(section: CatalogSection): List<CatalogEntry> = when (section) {
    CatalogSection.SYMBOLS -> allSymbols
    CatalogSection.EMOJIS -> allEmojis
    CatalogSection.KAOMOJI -> allKaomoji
    CatalogSection.LENNY -> allLennyFaces
  }

  fun filterEntries(
    section: CatalogSection,
    query: String,
    categoryId: String,
    favoriteGlyphs: Set<String> = emptySet(),
  ): List<CatalogEntry> {
    val q = query.trim().lowercase()
    return entriesForSection(section).filter { item ->
      val matchesCategory = when (categoryId) {
        "all" -> true
        "favorites" -> favoriteGlyphs.contains(item.glyph)
        else -> item.categoryId == categoryId
      }
      if (!matchesCategory) return@filter false
      if (q.isEmpty()) return@filter true
      item.glyph.lowercase().contains(q) ||
        item.name.lowercase().contains(q) ||
        item.categoryLabel.lowercase().contains(q) ||
        item.codepointSummary.lowercase().contains(q) ||
        item.tags.any { it.contains(q) }
    }
  }

  fun observeFavorites(): Flow<List<FavoriteSymbolEntity>> = vaultDao.observeFavorites()

  fun observeFavoritesByMostUsed(): Flow<List<FavoriteSymbolEntity>> = vaultDao.observeFavoritesByMostUsed()

  fun observeRecentCopies(): Flow<List<RecentCopyEntity>> = vaultDao.observeRecentCopies()

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
    vaultDao.deleteRecentByText(text)
    vaultDao.insertRecent(RecentCopyEntity(text = text, sourceLabel = sourceLabel))
    vaultDao.trimRecentCopies()
    vaultDao.incrementFavoriteUsage(text)
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
