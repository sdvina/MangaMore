package org.sdvina.mangamore.data.local.dao

import androidx.room.*
import org.sdvina.mangamore.data.local.entity.Tag
import org.sdvina.mangamore.data.local.entity.TagWithComics

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg tags: Tag)

    @Query("SELECT * FROM tag")
    fun getAll(): List<Tag>

    @Delete
    fun delete(tag: Tag)

    @Delete
    fun deleteComics(vararg tags: Tag)

    @Transaction
    @Query("SELECT * FROM tag")
    fun getTagsWithComics(): List<TagWithComics>
}