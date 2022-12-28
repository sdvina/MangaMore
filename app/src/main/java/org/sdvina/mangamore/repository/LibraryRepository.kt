package org.sdvina.mangamore.repository

import ando.file.core.FileLogger
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.paging.PagingSource
import org.sdvina.mangamore.data.enums.FolderType
import org.sdvina.mangamore.data.local.AppDatabaseHelper
import org.sdvina.mangamore.data.local.AppPreferences
import org.sdvina.mangamore.data.local.entity.Comic
import org.sdvina.mangamore.data.local.entity.Folder
import org.sdvina.mangamore.data.local.storage.DiskCache
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.data.model.ComicItemDetail
import org.sdvina.mangamore.data.model.FolderItem

class LibraryRepository(val context: Context) {
    private val comicDao = AppDatabaseHelper.db.getComicDao()
    private val folderDao = AppDatabaseHelper.db.getFolderDao()


    private val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
// Check for the freshest data.


    fun getFolderItems(): PagingSource<Int, FolderItem> {
        return folderDao.pagingSource()
    }

    fun getComicItemsPagingSourceByFolderId(
        accountId: Long,
        folderId: Long
    ): PagingSource<Int, ComicItem> {
        return comicDao.pagingSourceByFolderId(accountId, folderId)
    }

    fun getComicItemsPagingSource(accountId: Long): PagingSource<Int, ComicItem> {
        return comicDao.pagingSource(accountId)
    }

    suspend fun addFolder(folderUri: Uri?) {
        val contentResolver = context.contentResolver
        val file = DocumentFile.fromTreeUri(context, folderUri!!)!!
        if (file.isDirectory && file.name != null) {
            folderDao.insertFolders(
                Folder(name = file.name!!, uri = folderUri, thumbnailUri = null, type = FolderType.LOCAL)
            )
            var thumbnailUri: Uri? = null
            var flag = 0
            file.listFiles().forEach {
                    DiskCache.extractComicItemDetails(context, it)?.let { comicItemDetail ->
                        if(flag++ < 1) { // 拿住 一张 封面
                            thumbnailUri = comicItemDetail.frontCoverUri
                            FileLogger.v("获取文件夹封面", thumbnailUri.toString())
                        }
                        addComic(folderUri, comicItemDetail)
                    } }

            thumbnailUri?.let { folderDao.updateFolderThumbnailUriByUri(folderUri, it) }
            contentResolver.takePersistableUriPermission(folderUri, takeFlags) // 永久授权
        }
    }

    private suspend fun addComic(folderUri: Uri, comicItemDetail: ComicItemDetail){
       val ids = comicDao.insertComics(
            Comic(
            name = comicItemDetail.comicItemName,
            folderId = folderDao.getFolderByUri(folderUri).folderId,
            uri = comicItemDetail.comicItemUri,
            thumbnailUri = comicItemDetail.frontCoverUri.toString(),
            type = comicItemDetail.comicItemType,
            pageTotal = comicItemDetail.comicPageItems!!.size,
            size = null
        )) // 存储到ROOM
        comicItemDetail.comicId = ids.first()
        AppPreferences.setComicItemDetail(comicItemDetail) // 存储到缓存
    }
}