package org.jayhsu.mangamore.data.local

import android.net.Uri
import androidx.room.TypeConverter
import org.jayhsu.mangamore.data.enums.ComicType
import org.jayhsu.mangamore.data.enums.FolderType
import org.jayhsu.mangamore.data.enums.ReadStatus
import java.sql.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromUri(value: String?): Uri? = value?.let { Uri.parse(Uri.decode(it)) }

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun fromFolderType(type: FolderType): Int = type.id

    @TypeConverter
    fun toFolderType(id: Int): FolderType = FolderType.values()[id-1]

    @TypeConverter
    fun fromComicType(type: ComicType): Int = type.id

    @TypeConverter
    fun toComicType(id: Int): ComicType = ComicType.values()[id-1]

    @TypeConverter
    fun fromReadStatus(type: ReadStatus): Int = type.id

    @TypeConverter
    fun toReadStatus(id: Int): ReadStatus = ReadStatus.values()[id-1]
}