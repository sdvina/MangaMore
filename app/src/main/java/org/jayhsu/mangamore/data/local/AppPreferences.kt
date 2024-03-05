package org.jayhsu.mangamore.data.local

import androidx.core.content.edit
import com.tencent.mmkv.MMKV
import org.jayhsu.mangamore.data.model.ComicItemDetail

object AppPreferences {

    private val mmkv = MMKV.defaultMMKV()
    private const val CURRENT_ACCOUNT_ID = "current_account_id"
    private const val LAST_VIEWED_FEED_ID = "last_viewed_comic_id"
    private const val THEME = "theme"
    private const val COMIC_PREFIX = "comic/"

    var currentAccountId: Long
        get() = mmkv.getLong(CURRENT_ACCOUNT_ID, 1)
        set(value) = mmkv.edit { this.putLong(CURRENT_ACCOUNT_ID, value) }

    var lastViewedComicId: Long
        get() = mmkv.getLong(LAST_VIEWED_FEED_ID, 0)
        set(value) = mmkv.edit { this.putLong(LAST_VIEWED_FEED_ID, value) }

    var theme: Int
        get() = mmkv.getInt(THEME, 0)
        set(value) = mmkv.edit { this.putInt(THEME, value) }

    fun setComicItemDetail(value: ComicItemDetail ) {
        mmkv.encode(COMIC_PREFIX + value.comicId, value)
    }

    fun getComicItemDetail(comicId: Long): ComicItemDetail? {
        return mmkv.decodeParcelable(COMIC_PREFIX + comicId, ComicItemDetail::class.java)
    }
}