package org.sdvina.mangamore.data.local.dao

import androidx.room.*
import org.sdvina.mangamore.data.local.entity.Progress

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg progresses: Progress)

    @Query("SELECT * FROM progress")
    fun getAll(): List<Progress>

    @Delete
    fun delete(progress: Progress)

    @Delete
    fun deleteBookmarks(vararg progresses: Progress)
}