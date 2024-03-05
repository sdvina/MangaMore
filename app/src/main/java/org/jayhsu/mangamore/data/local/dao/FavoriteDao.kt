package org.jayhsu.mangamore.data.local.dao

import androidx.room.*
import org.jayhsu.mangamore.data.local.entity.Favorite

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg favorites: Favorite)

    @Query("SELECT * FROM favorite")
    fun getAll(): List<Favorite>

    @Delete
    fun delete(favorite: Favorite)

    @Delete
    fun deleteFavorites(vararg favorites: Favorite)
}