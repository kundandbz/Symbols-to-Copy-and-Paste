package com.example.ui

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppColorPalette
import com.example.data.AppThemeMode
import com.example.data.LennyBuilderParts
import com.example.data.SymbolRepository
import com.example.data.ThemePreferencesRepository
import com.example.data.ThemeSettings
import com.example.data.local.FavoriteSymbolEntity
import com.example.data.local.RecentCopyEntity
import com.example.data.local.SavedSnippetEntity
import com.example.model.BioFrameTemplate
import com.example.model.BioToolsEngine
import com.example.model.CatalogCategory
import com.example.model.CatalogEntry
import com.example.model.CatalogSection
import com.example.model.UnicodeTextUtils
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class BannerTone {
  COPIED,
  ADDED,
  SAVED,
  FAVORITED,
  UNFAVORITED,
}

enum class FavoriteSortMode(val label: String) {
  MOST_USED("Most Used"),
  RECENT("Recently Added"),
}

@Immutable
data class CopyBannerState(
  val text: String,
  val label: String,
  val tone: BannerTone = BannerTone.COPIED,
  val id: Long = 0L,
)

@Immutable
data class ComposerUiState(
  val composerText: String = "",
  val isBuildMode: Boolean = false,
)

@Immutable
data class SymbolsUiState(
  val searchQuery: String = "",
  val selectedCategoryId: String = "all",
  val filteredSymbols: List<CatalogEntry> = emptyList(),
  val groupedSections: List<Pair<CatalogCategory, List<CatalogEntry>>>? = null,
  val trendingSymbols: List<CatalogEntry> = emptyList(),
  val categoryCounts: Map<String, Int> = emptyMap(),
  val favoriteSymbolCount: Int = 0,
  val totalSymbolCount: Int = 0,
)

@Immutable
data class SectionBrowseUiState(
  val searchQuery: String = "",
  val selectedCategoryId: String = "all",
  val items: List<CatalogEntry> = emptyList(),
  val groupedSections: List<Pair<CatalogCategory, List<CatalogEntry>>>? = null,
  val totalCount: Int = 0,
)

@Immutable
data class LennyBuilderUiState(
  val earIndex: Int = 0,
  val eyeIndex: Int = 0,
  val mouthIndex: Int = 0,
  val accessoryIndex: Int = 0,
) {
  val builtFace: String
    get() = LennyBuilderParts.build(earIndex, eyeIndex, mouthIndex, accessoryIndex)
}

enum class ToolTab(val label: String) {
  FONTS("Fancy Fonts"),
  BIO_TAGS("Bio & Clan"),
  INVISIBLE("Invisible Text"),
  CONVERTERS("Converters"),
}

@Immutable
data class ToolsUiState(
  val activeTool: ToolTab = ToolTab.FONTS,
  val fontInput: String = "Symbols to Copy",
  val activeFontFrame: BioFrameTemplate? = null,
  val bioInput: String = "Nova",
  val spacerLineCount: Int = 3,
  val converterInput: String = "HELLO WORLD",
  val isMorseDecode: Boolean = false,
)

@Immutable
data class VaultUiState(
  val activeTab: Int = 0, // 0 = Favorites, 1 = Recent, 2 = Saved Snippets
  val favoriteSortMode: FavoriteSortMode = FavoriteSortMode.MOST_USED,
  val favoritesQuery: String = "",
  val favorites: List<FavoriteSymbolEntity> = emptyList(),
  val totalFavoritesCount: Int = 0,
  val mostUsedFavorites: List<FavoriteSymbolEntity> = emptyList(),
  val favoriteGlyphs: Set<String> = emptySet(),
  val recentCopies: List<RecentCopyEntity> = emptyList(),
  val savedSnippets: List<SavedSnippetEntity> = emptyList(),
)

private data class FavoritesSnapshot(
  val sortMode: FavoriteSortMode,
  val query: String,
  val displayed: List<FavoriteSymbolEntity>,
  val totalCount: Int,
  val mostUsed: List<FavoriteSymbolEntity>,
  val glyphSet: Set<String>,
)

class SymbolPadViewModel(
  private val repository: SymbolRepository,
  private val themePreferences: ThemePreferencesRepository? = null,
) : ViewModel() {

  // ── Theme & Appearance State ──
  private val _fallbackThemeSettings = MutableStateFlow(ThemeSettings())
  val themeSettings: StateFlow<ThemeSettings> =
    themePreferences?.themeSettings?.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = ThemeSettings(),
    ) ?: _fallbackThemeSettings

  // ── Shared Room Favorites Flows (isolated so recentCopies changes don't recompose browse tabs) ──
  private val recentFavoritesFlow: StateFlow<List<FavoriteSymbolEntity>> =
    repository.observeFavorites().stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptyList(),
    )

  val mostUsedFavorites: StateFlow<List<FavoriteSymbolEntity>> =
    repository.observeFavoritesByMostUsed().stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptyList(),
    )

  val favoriteGlyphs: StateFlow<Set<String>> =
    recentFavoritesFlow
      .map { list -> list.mapTo(HashSet(list.size)) { it.glyph } as Set<String> }
      .distinctUntilChanged()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet(),
      )

  val favoritesCount: StateFlow<Int> =
    recentFavoritesFlow
      .map { it.size }
      .distinctUntilChanged()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
      )

  private val favoriteSymbolCountFlow: StateFlow<Int> =
    favoriteGlyphs
      .map { favs -> if (favs.isEmpty()) 0 else repository.allSymbols.count { it.glyph in favs } }
      .distinctUntilChanged()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
      )

  // ── Combo Builder Tray State (decoupled from Symbols filtering!) ──
  private val _composerState = MutableStateFlow(ComposerUiState())
  val composerState: StateFlow<ComposerUiState> = _composerState

  // ── Symbols Tab State ──
  private val _symbolsQuery = MutableStateFlow("")
  private val _symbolsCategoryId = MutableStateFlow("all")

  val symbolsState: StateFlow<SymbolsUiState> = combine(
    _symbolsQuery,
    _symbolsCategoryId,
    favoriteGlyphs,
    favoriteSymbolCountFlow,
  ) { query, catId, favGlyphs, favSymCount ->
    val isGroupedAll = catId == "all" && query.isBlank()
    SymbolsUiState(
      searchQuery = query,
      selectedCategoryId = catId,
      filteredSymbols = if (isGroupedAll) {
        repository.allSymbols
      } else {
        repository.filterEntries(CatalogSection.SYMBOLS, query, catId, favGlyphs)
      },
      groupedSections = if (isGroupedAll) repository.groupedSymbols else null,
      trendingSymbols = repository.trendingSymbols,
      categoryCounts = repository.symbolCategoryCounts,
      favoriteSymbolCount = favSymCount,
      totalSymbolCount = repository.allSymbols.size,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = SymbolsUiState(
      filteredSymbols = repository.allSymbols,
      groupedSections = repository.groupedSymbols,
      trendingSymbols = repository.trendingSymbols,
      categoryCounts = repository.symbolCategoryCounts,
      totalSymbolCount = repository.allSymbols.size,
    ),
  )

  // ── Emojis Tab State (lazy so EmojiCatalog is only loaded when Emojis tab is opened) ──
  private val _emojiQuery = MutableStateFlow("")
  private val _emojiCategoryId = MutableStateFlow("all")

  val emojiState: StateFlow<SectionBrowseUiState> by lazy(LazyThreadSafetyMode.NONE) {
    combine(
      _emojiQuery,
      _emojiCategoryId,
      favoriteGlyphs,
    ) { query, catId, favGlyphs ->
      val isGroupedAll = catId == "all" && query.isBlank()
      SectionBrowseUiState(
        searchQuery = query,
        selectedCategoryId = catId,
        items = if (isGroupedAll) {
          repository.allEmojis
        } else {
          repository.filterEntries(CatalogSection.EMOJIS, query, catId, favGlyphs)
        },
        groupedSections = if (isGroupedAll) repository.groupedEmojis else null,
        totalCount = repository.allEmojis.size,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = SectionBrowseUiState(
        items = repository.allEmojis,
        groupedSections = repository.groupedEmojis,
        totalCount = repository.allEmojis.size,
      ),
    )
  }

  // ── Kaomoji Tab State (lazy so KaomojiCatalog is only loaded when Kaomoji tab is opened) ──
  private val _kaomojiQuery = MutableStateFlow("")
  private val _kaomojiCategoryId = MutableStateFlow("all")

  val kaomojiState: StateFlow<SectionBrowseUiState> by lazy(LazyThreadSafetyMode.NONE) {
    combine(
      _kaomojiQuery,
      _kaomojiCategoryId,
      favoriteGlyphs,
    ) { query, catId, favGlyphs ->
      val isGroupedAll = catId == "all" && query.isBlank()
      SectionBrowseUiState(
        searchQuery = query,
        selectedCategoryId = catId,
        items = if (isGroupedAll) {
          repository.allKaomoji
        } else {
          repository.filterEntries(CatalogSection.KAOMOJI, query, catId, favGlyphs)
        },
        groupedSections = if (isGroupedAll) repository.groupedKaomoji else null,
        totalCount = repository.allKaomoji.size,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = SectionBrowseUiState(
        items = repository.allKaomoji,
        groupedSections = repository.groupedKaomoji,
        totalCount = repository.allKaomoji.size,
      ),
    )
  }

  // ── Lenny Tab State (lazy so LennyCatalog is only loaded when Lenny tab is opened) ──
  private val _lennyQuery = MutableStateFlow("")
  private val _lennyCategoryId = MutableStateFlow("all")

  val lennyState: StateFlow<SectionBrowseUiState> by lazy(LazyThreadSafetyMode.NONE) {
    combine(
      _lennyQuery,
      _lennyCategoryId,
      favoriteGlyphs,
    ) { query, catId, favGlyphs ->
      val isGroupedAll = catId == "all" && query.isBlank()
      SectionBrowseUiState(
        searchQuery = query,
        selectedCategoryId = catId,
        items = if (isGroupedAll) {
          repository.allLennyFaces
        } else {
          repository.filterEntries(CatalogSection.LENNY, query, catId, favGlyphs)
        },
        groupedSections = if (isGroupedAll) repository.groupedLenny else null,
        totalCount = repository.allLennyFaces.size,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = SectionBrowseUiState(
        items = repository.allLennyFaces,
        groupedSections = repository.groupedLenny,
        totalCount = repository.allLennyFaces.size,
      ),
    )
  }

  private val _lennyBuilder = MutableStateFlow(LennyBuilderUiState())
  val lennyBuilder: StateFlow<LennyBuilderUiState> = _lennyBuilder

  // ── Tools Tab State ──
  private val _toolsState = MutableStateFlow(ToolsUiState())
  val toolsState: StateFlow<ToolsUiState> = _toolsState

  // ── Shared Detail Sheet & Copy Banner ──
  private val _detailEntry = MutableStateFlow<CatalogEntry?>(null)
  val detailEntry: StateFlow<CatalogEntry?> = _detailEntry

  private val _copyBanner = MutableStateFlow<CopyBannerState?>(null)
  val copyBanner: StateFlow<CopyBannerState?> = _copyBanner

  private val bannerIdCounter = AtomicLong(0L)
  private var bannerDismissJob: Job? = null

  // ── Vault & Favorites State (lazy so recent/snippets Room queries only subscribe on Vault screen) ──
  private val _vaultTab = MutableStateFlow(0)
  private val _favoriteSortMode = MutableStateFlow(FavoriteSortMode.MOST_USED)
  private val _favoritesQuery = MutableStateFlow("")

  private val favoritesSnapshotFlow by lazy(LazyThreadSafetyMode.NONE) {
    combine(
      _favoriteSortMode,
      _favoritesQuery,
      recentFavoritesFlow,
      mostUsedFavorites,
    ) { sortMode, query, recentFavs, mostUsedFavs ->
      val base = when (sortMode) {
        FavoriteSortMode.MOST_USED -> mostUsedFavs
        FavoriteSortMode.RECENT -> recentFavs
      }
      val trimmed = query.trim().lowercase()
      val filtered = if (trimmed.isEmpty()) {
        base
      } else {
        base.filter { fav ->
          fav.glyph.lowercase().contains(trimmed) ||
            fav.label.lowercase().contains(trimmed) ||
            fav.categoryName.lowercase().contains(trimmed)
        }
      }
      FavoritesSnapshot(
        sortMode = sortMode,
        query = query,
        displayed = filtered,
        totalCount = recentFavs.size,
        mostUsed = mostUsedFavs,
        glyphSet = recentFavs.mapTo(HashSet(recentFavs.size)) { it.glyph },
      )
    }
  }

  val vaultState: StateFlow<VaultUiState> by lazy(LazyThreadSafetyMode.NONE) {
    combine(
      _vaultTab,
      favoritesSnapshotFlow,
      repository.observeRecentCopies(),
      repository.observeSnippets(),
    ) { tab, favSnap, recent, snippets ->
      VaultUiState(
        activeTab = tab,
        favoriteSortMode = favSnap.sortMode,
        favoritesQuery = favSnap.query,
        favorites = favSnap.displayed,
        totalFavoritesCount = favSnap.totalCount,
        mostUsedFavorites = favSnap.mostUsed,
        favoriteGlyphs = favSnap.glyphSet,
        recentCopies = recent,
        savedSnippets = snippets,
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = VaultUiState(),
    )
  }

  // ── Symbols actions ──
  fun onSymbolsQueryChange(query: String) {
    _symbolsQuery.value = query
  }

  fun onSymbolsCategorySelected(categoryId: String) {
    _symbolsCategoryId.value = categoryId
  }

  fun toggleBuildMode(enabled: Boolean) {
    _composerState.update { it.copy(isBuildMode = enabled) }
  }

  fun onComposerTextChange(text: String) {
    _composerState.update { it.copy(composerText = text) }
  }

  fun appendToComposer(text: String) {
    _composerState.update { it.copy(composerText = it.composerText + text) }
  }

  fun backspaceComposer() {
    _composerState.update { current ->
      current.copy(composerText = UnicodeTextUtils.removeLastGrapheme(current.composerText))
    }
  }

  fun clearComposer() {
    _composerState.update { it.copy(composerText = "") }
  }

  // ── Emojis actions ──
  fun onEmojiQueryChange(query: String) {
    _emojiQuery.value = query
  }

  fun onEmojiCategorySelected(categoryId: String) {
    _emojiCategoryId.value = categoryId
  }

  // ── Kaomoji actions ──
  fun onKaomojiQueryChange(query: String) {
    _kaomojiQuery.value = query
  }

  fun onKaomojiCategorySelected(categoryId: String) {
    _kaomojiCategoryId.value = categoryId
  }

  // ── Lenny actions ──
  fun onLennyQueryChange(query: String) {
    _lennyQuery.value = query
  }

  fun onLennyCategorySelected(categoryId: String) {
    _lennyCategoryId.value = categoryId
  }

  fun onSelectLennyEar(index: Int) {
    _lennyBuilder.update { it.copy(earIndex = index) }
  }

  fun onSelectLennyEye(index: Int) {
    _lennyBuilder.update { it.copy(eyeIndex = index) }
  }

  fun onSelectLennyMouth(index: Int) {
    _lennyBuilder.update { it.copy(mouthIndex = index) }
  }

  fun onSelectLennyAccessory(index: Int) {
    _lennyBuilder.update { it.copy(accessoryIndex = index) }
  }

  fun randomizeLenny() {
    _lennyBuilder.value = LennyBuilderUiState(
      earIndex = Random.nextInt(LennyBuilderParts.ears.size),
      eyeIndex = Random.nextInt(LennyBuilderParts.eyes.size),
      mouthIndex = Random.nextInt(LennyBuilderParts.mouths.size),
      accessoryIndex = Random.nextInt(LennyBuilderParts.accessories.size),
    )
  }

  // ── Shared catalog interactions ──
  fun openEntryDetail(entry: CatalogEntry) {
    _detailEntry.value = entry
  }

  fun dismissEntryDetail() {
    _detailEntry.value = null
  }

  fun onEntryTapped(entry: CatalogEntry, onCopyToSystemClipboard: (String) -> Unit) {
    if (_composerState.value.isBuildMode) {
      appendToComposer(entry.glyph)
      showBanner(text = entry.glyph, label = entry.name, tone = BannerTone.ADDED)
    } else {
      copyText(entry.glyph, entry.name, onCopyToSystemClipboard)
    }
  }

  fun appendEntryToTray(entry: CatalogEntry) {
    _composerState.update { it.copy(isBuildMode = true, composerText = it.composerText + entry.glyph) }
    showBanner(text = entry.glyph, label = entry.name, tone = BannerTone.ADDED)
  }

  private fun showBanner(text: String, label: String, tone: BannerTone) {
    val banner = CopyBannerState(
      text = text,
      label = label,
      tone = tone,
      id = bannerIdCounter.incrementAndGet(),
    )
    _copyBanner.value = banner
    bannerDismissJob?.cancel()
    bannerDismissJob = viewModelScope.launch {
      delay(2200)
      if (_copyBanner.value?.id == banner.id) {
        _copyBanner.value = null
      }
    }
  }

  fun copyText(text: String, label: String, onCopyToSystemClipboard: (String) -> Unit) {
    if (text.isEmpty()) return
    onCopyToSystemClipboard(text)
    showBanner(text = text, label = label, tone = BannerTone.COPIED)
    viewModelScope.launch {
      repository.recordCopy(text, label)
    }
  }

  fun dismissCopyBanner() {
    bannerDismissJob?.cancel()
    _copyBanner.value = null
  }

  fun onFavoriteTapped(favorite: FavoriteSymbolEntity, onCopyToSystemClipboard: (String) -> Unit) {
    if (_composerState.value.isBuildMode) {
      appendToComposer(favorite.glyph)
      showBanner(text = favorite.glyph, label = favorite.label, tone = BannerTone.ADDED)
    } else {
      copyText(favorite.glyph, favorite.label, onCopyToSystemClipboard)
    }
  }

  fun toggleFavorite(entry: CatalogEntry) {
    viewModelScope.launch {
      val added = repository.toggleFavorite(
        glyph = entry.glyph,
        label = entry.name,
        categoryName = "${entry.section.label} · ${entry.categoryLabel}",
      )
      showBanner(
        text = entry.glyph,
        label = entry.name,
        tone = if (added) BannerTone.FAVORITED else BannerTone.UNFAVORITED,
      )
    }
  }

  fun removeFavorite(glyph: String) {
    viewModelScope.launch {
      repository.removeFavorite(glyph)
      showBanner(text = glyph, label = glyph, tone = BannerTone.UNFAVORITED)
    }
  }

  fun clearRecentHistory() {
    viewModelScope.launch {
      repository.clearRecentCopies()
    }
  }

  fun saveSnippet(title: String, content: String, kind: String) {
    if (content.isEmpty()) return
    val displayTitle = title.ifBlank { "Custom Combo" }
    viewModelScope.launch {
      repository.saveSnippet(displayTitle, content, kind)
    }
    showBanner(text = content, label = displayTitle, tone = BannerTone.SAVED)
  }

  fun deleteSnippet(id: Long) {
    viewModelScope.launch {
      repository.deleteSnippet(id)
    }
  }

  // ── Tools actions ──
  fun onToolTabSelected(tab: ToolTab) {
    _toolsState.update { it.copy(activeTool = tab) }
  }

  fun onFontInputChange(text: String) {
    _toolsState.update { it.copy(fontInput = text) }
  }

  fun onSelectFontFrame(frame: BioFrameTemplate?) {
    _toolsState.update { state ->
      val next = if (state.activeFontFrame?.id == frame?.id) null else frame
      state.copy(activeFontFrame = next)
    }
  }

  fun onBioInputChange(text: String) {
    _toolsState.update { it.copy(bioInput = text) }
  }

  fun onSpacerLineCountChange(count: Int) {
    _toolsState.update { it.copy(spacerLineCount = count.coerceIn(1, 8)) }
  }

  fun onConverterInputChange(text: String) {
    _toolsState.update { it.copy(converterInput = text) }
  }

  fun onToggleMorseDecode(decode: Boolean) {
    _toolsState.update { state ->
      if (state.isMorseDecode == decode) return@update state
      val converted = if (decode) {
        BioToolsEngine.toMorse(state.converterInput)
      } else {
        BioToolsEngine.fromMorse(state.converterInput)
      }
      state.copy(
        isMorseDecode = decode,
        converterInput = converted.ifBlank { state.converterInput },
      )
    }
  }

  // ── Vault actions ──
  fun onVaultTabSelected(tab: Int) {
    _vaultTab.value = tab
  }

  fun onFavoriteSortModeSelected(mode: FavoriteSortMode) {
    _favoriteSortMode.value = mode
  }

  fun onFavoritesQueryChange(query: String) {
    _favoritesQuery.value = query
  }

  // ── Theme actions ──
  fun onThemeModeSelected(mode: AppThemeMode) {
    if (themePreferences != null) {
      viewModelScope.launch { themePreferences.setThemeMode(mode) }
    } else {
      _fallbackThemeSettings.update { it.copy(mode = mode) }
    }
  }

  fun onColorPaletteSelected(palette: AppColorPalette) {
    if (themePreferences != null) {
      viewModelScope.launch { themePreferences.setColorPalette(palette) }
    } else {
      _fallbackThemeSettings.update { it.copy(palette = palette) }
    }
  }

  class Factory(
    private val repository: SymbolRepository,
    private val themePreferences: ThemePreferencesRepository? = null,
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return SymbolPadViewModel(repository, themePreferences) as T
    }
  }
}
