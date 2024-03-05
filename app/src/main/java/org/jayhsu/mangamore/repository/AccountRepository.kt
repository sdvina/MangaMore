package org.jayhsu.mangamore.repository

import org.jayhsu.mangamore.data.local.AppDatabaseHelper
import org.jayhsu.mangamore.data.local.entity.Account

class AccountRepository {
    private val accountDao = AppDatabaseHelper.db.getAccountDao()

    suspend fun addAccount(account: Account){
        accountDao.insertAll(account)
    }

    fun getAccountByAccountId(accountId: Long): Account?{
        return accountDao.getAccountById(accountId)
    }
}