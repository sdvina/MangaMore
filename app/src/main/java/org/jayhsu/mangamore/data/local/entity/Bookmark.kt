package org.jayhsu.mangamore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "bookmark",
    indices = [Index(value = ["account_id", "comic_id", "page_no"], unique = true)])
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bookmark_id") val bookmarkId: Long,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "comic_id", index = true) val comicId: Long,
    @ColumnInfo(name = "page_no") val pageNo: Int,
    @ColumnInfo(name = "create_date") val create_date: Date
)