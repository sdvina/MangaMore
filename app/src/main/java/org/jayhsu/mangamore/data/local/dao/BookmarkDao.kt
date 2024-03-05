package org.jayhsu.mangamore.data.local.dao

import androidx.room.*
import org.jayhsu.mangamore.data.local.entity.Bookmark

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg bookmarks: Bookmark)

    @Query("SELECT * FROM bookmark")
    fun getAll(): List<Bookmark>

    @Delete
    fun delete(bookmark: Bookmark)

    @Delete
    fun deleteBookmarks(vararg bookmarks: Bookmark)
}