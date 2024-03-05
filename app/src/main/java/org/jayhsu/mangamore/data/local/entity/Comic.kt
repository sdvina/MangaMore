package org.jayhsu.mangamore.data.local.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.jayhsu.mangamore.data.enums.ComicType

@Entity(tableName = "comic", indices = [Index(value = ["uri"], unique = true)])
data class Comic(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comic_id") val comicId: Long = 0,
    @ColumnInfo(name = "folder_id") val folderId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "uri") val uri: Uri,
    @ColumnInfo(name = "thumbnail_uri") val thumbnailUri: String?,
    @ColumnInfo(name = "type") val type: ComicType,
    @ColumnInfo(name = "page_total") val pageTotal: Int,
    @ColumnInfo(name = "size") val size: Double? // MB
)