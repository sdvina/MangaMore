package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AccountWithTagsAndComics(
    @Embedded val account: Account,
    @Relation(
        entity = Tag::class,
        parentColumn = "account_id",
        entityColumn = "account_id"
    )
    val tags: List<TagWithComics>
)