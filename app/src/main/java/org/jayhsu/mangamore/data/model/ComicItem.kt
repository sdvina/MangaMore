package org.jayhsu.mangamore.data.model

import android.net.Uri
import androidx.room.ColumnInfo
import org.jayhsu.mangamore.data.enums.ComicType
import java.sql.Date

data class ComicItem (
    @ColumnInfo(name = "comic_id") val comicId: Long = 0,
    @ColumnInfo(name = "folder_id") val folderId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "uri") val uri: Uri,
    @ColumnInfo(name = "thumbnail_uri") val thumbnailUri: Uri?,
    @ColumnInfo(name = "type") val type: ComicType,
    @ColumnInfo(name = "page_total") val pageTotal: Int = 1,
    @ColumnInfo(name = "size") val size: Double?, // MB
    //
    @ColumnInfo(name = "page_no") val pageNo: Int = 0,
    @ColumnInfo(name = "progress_date") val progressDate: Date?,
    @ColumnInfo(name = "favorited") val favorited: Boolean = false
    )