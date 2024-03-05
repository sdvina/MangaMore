package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ComicWithProgresses(
    @Embedded val comic: Comic,
    @Relation(
        entity = Progress::class,
        parentColumn = "comic_id",
        entityColumn = "comic_id"
    )
    val progresses: List<Progress>
)