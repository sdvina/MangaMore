package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AccountWithComicsRefFavorite(
    @Embedded val account: Account,
    @Relation(
        entity = Comic::class,
        parentColumn = "account_id",
        entityColumn = "comic_id",
        associateBy = Junction(Favorite::class)
    )
    val comics: List<Comic>
)