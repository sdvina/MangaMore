package org.jayhsu.mangamore.data.enums

import org.jayhsu.mangamore.R

enum class ComicType(val id: Int, val resid: Int, val suffix: List<String>) {
    ZIP(1, R.string.comic_type_zip, listOf("zip")),
/*    RAR(2, R.string.comic_type_rar),
    LZMA(3, R.string.comic_type_lzma),
    TAR(4, R.string.comic_type_tar),
    TGZ(5, R.string.comic_type_tgz),
    PDF(6, R.string.comic_type_pdf),
    FOLDER(7, R.string.comic_type_folder);*/
}

enum class ImageType(val id: Int, val suffix: List<String>){
    BITMAP(1, listOf("bmp", "jpg", "jpeg", "png")),
/*    VECTOR(2, listOf()),
    GIF(3, listOf()),
    VIDEO(4, listOf())*/
}