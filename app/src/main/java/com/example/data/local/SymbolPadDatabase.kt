package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
  entities = [
    FavoriteSymbolEntity::class,
    RecentCopyEntity::class,
    SavedSnippetEntity::class,
  ],
  version = 2,
  exportSchema = false,
)
abstract class SymbolPadDatabase : RoomDatabase() {
  abstract fun vaultDao(): VaultDao

  companion object {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE favorite_symbols ADD COLUMN useCount INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE favorite_symbols ADD COLUMN lastUsedAt INTEGER NOT NULL DEFAULT 0")
      }
    }

    @Volatile
    private var INSTANCE: SymbolPadDatabase? = null

    fun getInstance(context: Context): SymbolPadDatabase {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.applicationContext,
          SymbolPadDatabase::class.java,
          "symbol_pad.db",
        )
          .addMigrations(MIGRATION_1_2)
          .build()
          .also { INSTANCE = it }
      }
    }
  }
}
