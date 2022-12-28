package org.sdvina.mangamore.data.local.dao

import android.net.Uri
import androidx.paging.PagingSource
import androidx.room.*
import org.sdvina.mangamore.data.local.entity.Folder
import org.sdvina.mangamore.data.local.entity.FolderWithComics
import org.sdvina.mangamore.data.model.FolderItem

@Dao
interface FolderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(vararg folders: Folder)

    @Query("SELECT folder.*, count(comic.comic_id) AS comic_count FROM folder " +
            "LEFT JOIN comic on folder.folder_id = comic.folder_id " +
            "GROUP BY folder.folder_id " +
            "ORDER BY folder.folder_id DESC ")
    fun pagingSource(): PagingSource<Int, FolderItem>

    @Query("SELECT * FROM folder WHERE uri = :uri")
    suspend fun getFolderByUri(uri: Uri): Folder

    @Query("UPDATE folder SET thumbnail_uri = :thumbnailUri WHERE uri = :uri")
    suspend fun updateFolderThumbnailUriByUri(uri: Uri, thumbnailUri: Uri)

    @Update
    suspend fun updateFolders(vararg folders: Folder)

    @Delete
    suspend fun deleteFolders(vararg folders: Folder)

    @Transaction
    @Query("SELECT * FROM folder")
    suspend fun getFolderWithComics(): List<FolderWithComics>
}