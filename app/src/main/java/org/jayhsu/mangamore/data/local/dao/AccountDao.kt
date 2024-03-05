package org.jayhsu.mangamore.data.local.dao

import androidx.room.*
import org.jayhsu.mangamore.data.local.entity.*

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg accounts: Account)

    @Query("SELECT * FROM account")
    fun getAll(): List<Account>

    @Query("SELECT * FROM account WHERE account_id = :accountId")
    fun getAccountById(accountId: Long): Account?

    @Delete
    fun delete(account: Account)

    @Delete
    fun deleteComics(vararg account: Account)

    @Transaction
    @Query("SELECT * FROM account")
    fun getAccountWithComicsRefProgress(): List<AccountWithComicsRefProgress>

    @Transaction
    @Query("SELECT * FROM account")
    fun getAccountWithComicsRefFavorite(): List<AccountWithComicsRefFavorite>

    @Transaction
    @Query("SELECT * FROM account")
    fun getAccountWithComicsRefBookmark(): List<AccountWithComicsRefBookmark>

    @Transaction
    @Query("SELECT * FROM account")
    fun getAccountWithTagsAndComics(): List<AccountWithTagsAndComics>
}