package org.jayhsu.mangamore.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ComicPageItem (
    var uri: Uri?,
    val pageName: String,
    val entryName: String,
    var data: ByteArray?,
    val comicUri: Uri,
    val comicName: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ComicPageItem

        if (uri != other.uri) return false
        if (pageName != other.pageName) return false
        if (entryName != other.entryName) return false
        if (data != null) {
            if (other.data == null) return false
            if (!data.contentEquals(other.data)) return false
        } else if (other.data != null) return false
        if (comicUri != other.comicUri) return false
        return comicName == other.comicName
    }

    override fun hashCode(): Int {
        var result = uri?.hashCode() ?: 0
        result = 31 * result + pageName.hashCode()
        result = 31 * result + entryName.hashCode()
        result = 31 * result + (data?.contentHashCode() ?: 0)
        result = 31 * result + comicUri.hashCode()
        result = 31 * result + comicName.hashCode()
        return result
    }
}