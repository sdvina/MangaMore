package org.sdvina.mangamore.data.local.entity

import android.net.Uri
import androidx.room.*
import org.sdvina.mangamore.data.enums.FolderType

@Entity(tableName = "folder", indices = [Index(value = ["uri"], unique = true)])
data class Folder(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "folder_id") var folderId: Long = 0,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "uri") var uri: Uri,
    @ColumnInfo(name = "thumbnail_uri") var thumbnailUri: Uri?,
    @ColumnInfo(name = "type") var type: FolderType?
    )