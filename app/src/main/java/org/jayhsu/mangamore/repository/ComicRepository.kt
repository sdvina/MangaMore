package org.jayhsu.mangamore.repository

import android.content.Context
import android.net.Uri
import androidx.paging.PagingSource
import org.jayhsu.mangamore.data.local.AppDatabaseHelper
import org.jayhsu.mangamore.data.local.entity.Comic
import org.jayhsu.mangamore.data.local.storage.file.ArchiveFileReader
import org.jayhsu.mangamore.data.model.ComicItem
import org.jayhsu.mangamore.data.model.ComicPageItem

class ComicRepository(val context: Context) {

    private val comicDao = AppDatabaseHelper.db.getComicDao()

    fun getPagingSource(comicId: Long, fileNodeKey: String): PagingSource<Int, ComicPageItem> {
        return ComicPagePagingSource(context, comicId, fileNodeKey)
    }

    suspend fun getComicItemById(accountId: Long, comicId: Long): ComicItem {
        return comicDao.getComicItem(accountId, comicId)
    }

    suspend fun getComicById(comicId: Long): Comic?{
        return comicDao.getComicById(comicId)
    }

    fun getComicPageData(comicPageItem: ComicPageItem): ByteArray {
        return ArchiveFileReader.extractZipEntryToByteArray(context, comicPageItem)!!
    }

    fun getComicPageItemUri(comicPageItem: ComicPageItem): Uri {
        return ArchiveFileReader.extractZipEntryToUri(context, comicPageItem)
    }
}