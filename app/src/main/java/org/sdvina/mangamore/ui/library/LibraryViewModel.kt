package org.sdvina.mangamore.ui.library

import android.net.Uri
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.sdvina.mangamore.data.local.AppPreferences
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.data.model.FolderItem
import org.sdvina.mangamore.repository.LibraryRepository

data class LibraryViewModelState(
    val bookmarks: Set<Boolean> = emptySet(),
    val folderItem: FolderItem? = null,
    val isLoading: Boolean = false,
    val messages: List<Pair<Long, String>> = emptyList(),
    val searchInput: String? = null,
)

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    private val pagingConfig = PagingConfig(pageSize = 20)
    private var _folderId: Long = 0
    private var _folderItem: MutableStateFlow<FolderItem?> = MutableStateFlow(null)
    private var _searchInput: MutableStateFlow<String?> = MutableStateFlow(null)
    private var _state = MutableStateFlow(LibraryViewModelState())
    val state: StateFlow<LibraryViewModelState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                _folderItem,
                _searchInput
            ) { folderItem, searchInput ->
                LibraryViewModelState(
                    folderItem = folderItem,
                    searchInput = searchInput
                )
            }.catch { throwable ->
                throw throwable // TODO
            }.collect { _state.value = it }
        }
    }

    fun getFolderItemsFlow(): Flow<PagingData<FolderItem>> {
        return Pager(pagingConfig) {
            libraryRepository.getFolderItems()
        }.flow.cachedIn(viewModelScope)
    }

    fun searchComicItemsFlow(): Flow<PagingData<ComicItem>> {
        return Pager(pagingConfig) {
            libraryRepository.getComicItemsPagingSource(AppPreferences.currentAccountId)
        }.flow.map { pagingData ->
            pagingData.filter { comicItem -> comicItem.folderId == _folderId }
        }.cachedIn(viewModelScope)
    }

    fun addFolder(folderUri: Uri?) {
        viewModelScope.launch {
            libraryRepository.addFolder(folderUri)
        }
    }

    fun selectFolderItem(folderItem: FolderItem) {
        _folderId = folderItem.folderId
        _state.update { currentState ->
            currentState.copy(folderItem = folderItem)
        }
    }

    companion object {
        fun provideFactory(
            libraryRepository: LibraryRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LibraryViewModel(libraryRepository) as T
            }
        }
    }
}