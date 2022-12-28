package org.sdvina.mangamore.data.model

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
) : Parcelable