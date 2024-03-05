package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ComicWithFavorites(
    @Embedded val comic: Comic,
    @Relation(
        entity = Favorite::class,
        parentColumn = "comic_id",
        entityColumn = "comic_id"
    )
    val favorites: List<Favorite>
)