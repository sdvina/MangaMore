package org.jayhsu.mangamore.data.local.dao

import androidx.room.*
import org.jayhsu.mangamore.data.local.entity.TagComicCrossRef

@Dao
interface TagComicCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tagComicCrossRefs: TagComicCrossRef)

    @Query("SELECT * FROM tag_comic")
    fun getAll(): List<TagComicCrossRef>

    @Delete
    fun delete(tagComicCrossRef: TagComicCrossRef)

    @Delete
    fun deleteComics(vararg tagComicCrossRefs: TagComicCrossRef)
}