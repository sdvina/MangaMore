package org.sdvina.mangamore.repository

import org.sdvina.mangamore.data.local.AppDatabaseHelper
import org.sdvina.mangamore.data.local.entity.Account

class AccountRepository {
    private val accountDao = AppDatabaseHelper.db.getAccountDao()

    suspend fun addAccount(account: Account){
        accountDao.insertAll(account)
    }

    fun getAccountByAccountId(accountId: Long): Account?{
        return accountDao.getAccountById(accountId)
    }
}