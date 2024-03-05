package org.jayhsu.mangamore.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import org.jayhsu.mangamore.data.enums.FolderType

data class FolderItem (
    @ColumnInfo(name = "folder_id") var folderId: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "uri") var uri: Uri,
    @ColumnInfo(name = "thumbnail_uri") var thumbnailUri: Uri?,
    @ColumnInfo(name = "type") var type: FolderType?,
    @ColumnInfo(name = "comic_count") var comicCount: Int?
)