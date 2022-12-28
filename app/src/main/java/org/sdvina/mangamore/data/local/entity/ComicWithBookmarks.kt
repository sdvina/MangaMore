package org.sdvina.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ComicWithBookmarks(
    @Embedded val comic: Comic,
    @Relation(
        entity = Bookmark::class,
        parentColumn = "comic_id",
        entityColumn = "comic_id"
    )
    val bookmarks: List<Bookmark>
)