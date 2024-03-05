package org.jayhsu.mangamore.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.jayhsu.mangamore.data.constant.DatabaseConstant

object AppDatabaseHelper {
    lateinit var db: AppDatabase

    // Migration path definition from version 1 to version 2.
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // TODO
        }
    }

    fun onCreate(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, DatabaseConstant.DB_NAME)
            .enableMultiInstanceInvalidation() // 支持多进程运行
            //.fallbackToDestructiveMigration() // 回退迁移
            .build()
    }
}