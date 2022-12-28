package org.sdvina.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AccountWithComicsRefProgress(
    @Embedded val account: Account,
    @Relation(
        entity = Comic::class,
        parentColumn = "account_id",
        entityColumn = "comic_id",
        associateBy = Junction(Progress::class)
    )
    val comics: List<Comic>
)