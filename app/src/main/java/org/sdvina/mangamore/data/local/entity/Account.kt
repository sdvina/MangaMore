package org.sdvina.mangamore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "account", indices = [Index(value = ["name"], unique = true)])
data class Account(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "avatar_uri") val avatarUri: String,
    @ColumnInfo(name = "enabled") val enabled: Boolean
    )