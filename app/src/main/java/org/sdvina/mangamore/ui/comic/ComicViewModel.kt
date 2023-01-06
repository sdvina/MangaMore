package org.sdvina.mangamore.ui.comic

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.sdvina.mangamore.data.local.AppPreferences
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.data.model.ComicItemDetail
import org.sdvina.mangamore.data.model.ComicPageItem
import org.sdvina.mangamore.repository.ComicRepository

class ComicViewModel(
    private val comicRepository: ComicRepository
): ViewModel(){
    private val pagingConfig = PagingConfig(pageSize = 5)
    private var comicItemDetail: ComicItemDetail? = null

    suspend fun getComicItem(comicId: Long): ComicItem {
        return comicRepository.getComicItemById(AppPreferences.currentAccountId, comicId)
    }
    fun getPagingFlow(comicId: Long): Flow<PagingData<ComicPageItem>> {
        return Pager(pagingConfig){
            comicRepository.getPagingSource(comicId, "")
        }.flow.cachedIn(viewModelScope)
    }

    fun getComicItemDetail(comicId: Long): ComicItemDetail {
        comicItemDetail = AppPreferences.getComicItemDetail(comicId)
        return comicItemDetail!!  //  TODO  处理 找不到的情况
    }

    fun getComicPageItemUri(comicPageItem: ComicPageItem): Uri{
        return comicRepository.getComicPageItemUri(comicPageItem)
    }

    fun getComicPageData(comicPageItem: ComicPageItem): ByteArray{
        return comicRepository.getComicPageData(comicPageItem)
    }

    companion object {
        fun provideFactory(
            comicRepository: ComicRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ComicViewModel(comicRepository) as T
            }
        }
    }
}