package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagWithComics(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "comic_id",
        associateBy = Junction(TagComicCrossRef::class)
    )
    val comics: List<Comic>
)