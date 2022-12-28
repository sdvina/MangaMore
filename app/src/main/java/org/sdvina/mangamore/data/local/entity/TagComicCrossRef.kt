package org.sdvina.mangamore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "tag_comic", primaryKeys = ["tag_id", "comic_id"],
    indices = [Index(value = ["tag_id"]), Index(value = ["comic_id"])])
data class TagComicCrossRef(
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "comic_id") val comicId: Long
)