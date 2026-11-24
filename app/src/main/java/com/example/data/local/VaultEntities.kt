package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_symbols")
data class FavoriteSymbolEntity(
  @PrimaryKey val glyph: String,
  val label: String,
  val categoryName: String,
  val addedAt: Long = System.currentTimeMillis(),
  val useCount: Int = 0,
  val lastUsedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "recent_copies")
data class RecentCopyEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val text: String,
  val sourceLabel: String,
  val copiedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val content: String,
  val kind: String,
  val createdAt: Long = System.currentTimeMillis(),
)
