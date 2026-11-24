package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {

  // ── Favorites ──
  @Query("SELECT * FROM favorite_symbols ORDER BY addedAt DESC")
  fun observeFavorites(): Flow<List<FavoriteSymbolEntity>>

  @Query("SELECT * FROM favorite_symbols ORDER BY useCount DESC, lastUsedAt DESC, addedAt DESC")
  fun observeFavoritesByMostUsed(): Flow<List<FavoriteSymbolEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertFavorite(entity: FavoriteSymbolEntity)

  @Query("UPDATE favorite_symbols SET useCount = useCount + 1, lastUsedAt = :usedAt WHERE glyph = :glyph")
  suspend fun incrementFavoriteUsage(glyph: String, usedAt: Long = System.currentTimeMillis())

  @Query("DELETE FROM favorite_symbols WHERE glyph = :glyph")
  suspend fun removeFavorite(glyph: String)

  @Query("DELETE FROM favorite_symbols")
  suspend fun clearFavorites()

  @Query("SELECT EXISTS(SELECT 1 FROM favorite_symbols WHERE glyph = :glyph)")
  suspend fun isFavorite(glyph: String): Boolean

  // ── Recent Copies ──
  @Query("SELECT * FROM recent_copies ORDER BY copiedAt DESC LIMIT 50")
  fun observeRecentCopies(): Flow<List<RecentCopyEntity>>

  @Query("DELETE FROM recent_copies WHERE text = :text")
  suspend fun deleteRecentByText(text: String)

  @Insert
  suspend fun insertRecent(entity: RecentCopyEntity)

  @Query("DELETE FROM recent_copies WHERE id NOT IN (SELECT id FROM recent_copies ORDER BY copiedAt DESC LIMIT 50)")
  suspend fun trimRecentCopies()

  @Query("DELETE FROM recent_copies")
  suspend fun clearRecentCopies()

  // ── Saved Snippets ──
  @Query("SELECT * FROM saved_snippets ORDER BY createdAt DESC")
  fun observeSnippets(): Flow<List<SavedSnippetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSnippet(entity: SavedSnippetEntity)

  @Query("DELETE FROM saved_snippets WHERE id = :id")
  suspend fun deleteSnippet(id: Long)
}
