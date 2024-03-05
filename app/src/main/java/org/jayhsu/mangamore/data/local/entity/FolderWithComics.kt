package org.jayhsu.mangamore.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FolderWithComics(
    @Embedded val folder: Folder,
    @Relation(
        entity = Comic::class,
        parentColumn = "folder_id",
        entityColumn = "folder_id"
    )
    val comics: List<Comic>
)
