package org.jayhsu.mangamore.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import org.jayhsu.mangamore.data.local.entity.*
import org.jayhsu.mangamore.data.model.ComicItem

@Dao
interface ComicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComics(vararg comics: Comic): List<Long>

    @Query("SELECT * FROM comic WHERE uri = :folderUri ORDER BY comic_id DESC ")
    fun pagingSource(folderUri: String): PagingSource<Int, Comic>

    @Query("SELECT comic.*, progress.page_no, progress.create_date AS progress_date, ifnull(favorite.favorite_id, 0) AS favorited  FROM comic " +
            "LEFT JOIN progress ON comic.comic_id = progress.comic_id " +
            "LEFT JOIN favorite ON comic.comic_id = favorite.comic_id " +
            "WHERE progress.account_id = :accountId AND favorite.account_id = :accountId AND comic.folder_id = :folderId " +
            "ORDER BY comic.comic_id DESC")
    fun pagingSourceByFolderId(accountId: Long, folderId: Long): PagingSource<Int, ComicItem>

    @Query("SELECT comic.*, progress.page_no, progress.create_date AS progress_date, ifnull(favorite.favorite_id, 0) AS favorited  FROM comic " +
            "LEFT JOIN progress ON comic.comic_id = progress.comic_id AND progress.account_id = :accountId " +
            "LEFT JOIN favorite ON comic.comic_id = favorite.comic_id AND favorite.account_id = :accountId " +
            "ORDER BY comic.comic_id DESC")
    fun pagingSource(accountId: Long): PagingSource<Int, ComicItem>

    @Query("SELECT comic.*, progress.page_no, progress.create_date AS progress_date, ifnull(favorite.favorite_id, 0) AS favorited  FROM comic " +
            "LEFT JOIN progress ON comic.comic_id = progress.comic_id " +
            "LEFT JOIN favorite ON comic.comic_id = favorite.comic_id " +
            "WHERE progress.account_id = :accountId AND favorite.account_id = :accountId AND comic.comic_id = :comicId")
    suspend fun getComicItem(accountId: Long, comicId: Long): ComicItem

    @Query("SELECT * FROM comic WHERE comic_id = :comicId")
    suspend fun getComicById(comicId: Long): Comic?

    @Delete
    suspend fun deleteComic(comic: Comic)

    @Delete
    suspend fun deleteComics(vararg comics: Comic)

    @Transaction
    @Query("SELECT * FROM comic")
    suspend fun getComicsWithTags(): List<ComicWithTags>
}
