package org.sdvina.mangamore.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.sdvina.mangamore.data.enums.ComicType

@Parcelize
data class ComicItemDetail(
    var comicId: Long?,
    val comicItemName: String,
    val comicItemUri: Uri,
    val frontCoverUri: Uri?,
    val comicItemType: ComicType, //  List  展平
    val comicPageItems: MutableList<ComicPageItem>?
) : Parcelable
