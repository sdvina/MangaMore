package org.sdvina.mangamore.ui.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.*
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.local.AppPreferences
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.repository.LibraryRepository
import java.util.*

enum class ComicTopType(value: Int, @StringRes resId: Int){
    TOP_READING(1, R.string.top_reading),
    READING(2, R.string.reading),
    UNREAD(3, R.string.unread),
    READ(4, R.string.read),
    FAVORITED(5, R.string.favorited)
}

private data class HomeViewModelState(
    val isLoading: Boolean = false,
    val messages: List<Pair<Long, String>> = emptyList(),
    val searchInput: String = "",
)

class HomeViewModel(
    private val libraryRepository: LibraryRepository
): ViewModel() {
    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))
    private val pagingConfig = PagingConfig(pageSize = 20)

/*    fun getReadStusComicItemsFlow(accountId: Long, readStatus: ReadStatus): Flow<PagingData<ComicItem>> {
         return Pager(pagingConfig) {
              libraryRepository.getComicItemsByReadStatus(accountId, readStatus)
         }.flow.cachedIn(viewModelScope)
    }

    fun getFavoritedComicItemsFlow(accountId: Long): Flow<PagingData<ComicItem>> {
        return Pager(pagingConfig) {
            libraryRepository.getFavoritedComicItems(accountId)
        }.flow.cachedIn(viewModelScope)
    }*/

    fun getComicItemsFlow(type: ComicTopType): Flow<PagingData<ComicItem>> {
        return Pager(pagingConfig) {
            libraryRepository.getComicItemsPagingSource(AppPreferences.currentAccountId)
        }.flow.map{pagingData ->
               pagingData.filter { comicItem ->
                   when(type){
                       ComicTopType.READING -> (comicItem.pageNo!= 0 && comicItem.pageNo < comicItem.pageTotal)
                       ComicTopType.UNREAD -> comicItem.pageNo == 0
                       ComicTopType.READ -> comicItem.pageNo == comicItem.pageTotal
                       ComicTopType.FAVORITED -> comicItem.favorited
                       else -> false
               }
            }
        }.cachedIn(viewModelScope)
    }

    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    companion object {
        fun provideFactory(
            libraryRepository: LibraryRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(libraryRepository) as T
            }
        }
    }
}