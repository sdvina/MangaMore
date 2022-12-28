package org.sdvina.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ComicWithTags(
    @Embedded val comic: Comic,
    @Relation(
        parentColumn = "comic_id",
        entityColumn = "tag_id",
        associateBy = Junction(TagComicCrossRef::class)
    )
    val tags: List<Tag>
)