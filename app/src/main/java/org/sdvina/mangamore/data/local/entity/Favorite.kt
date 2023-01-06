package org.sdvina.mangamore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "favorite",indices = [Index(value = ["account_id", "comic_id"], unique = true)])
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "favorite_id") val favoriteId: Long,
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "comic_id", index = true) val comicId: Long,
    @ColumnInfo(name = "create_date") val createDate: Date
    )