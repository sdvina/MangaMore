package org.jayhsu.mangamore.data.local


import androidx.room.*
import org.jayhsu.mangamore.data.local.dao.*
import org.jayhsu.mangamore.data.local.entity.*

@Database(entities = [
    Account::class, Comic::class, Folder::class, Bookmark::class,
    Tag::class, Favorite::class, TagComicCrossRef::class,
    Progress::class,
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao
    abstract fun getComicDao(): ComicDao
    abstract fun getFolderDao(): FolderDao
    abstract fun getBookmarkDao(): BookmarkDao
    abstract fun getTagDao(): TagDao
    abstract fun getFavoriteDao(): FavoriteDao
    abstract fun getTagComicCrossRefDao(): TagComicCrossRefDao
    abstract fun getProgressDao(): ProgressDao
}