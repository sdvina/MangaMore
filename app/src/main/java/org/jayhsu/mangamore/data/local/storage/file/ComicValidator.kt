package org.jayhsu.mangamore.data.local.storage.file

import org.jayhsu.mangamore.data.enums.ComicType
import org.jayhsu.mangamore.data.enums.ImageType

object ComicValidator {

    private const val EXTENSION_SEPARATOR = "."

    // 受支持的压缩格式
    fun validateComicType(fileName: String): ComicType? {
        if (fileName.contains(EXTENSION_SEPARATOR)){
            ComicType.entries.forEach {
                if(it.suffix.contains(fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR) + 1).lowercase()))
                    return it
            }
        }
        return null
    }

    // 受支持的图像格式
    fun validateImageType(fileName: String): ImageType? {
        if (fileName.contains(EXTENSION_SEPARATOR)) {
            ImageType.entries.forEach {
                if(it.suffix.contains(fileName.substring(fileName.lastIndexOf(EXTENSION_SEPARATOR) + 1).lowercase()))
                    return it
            }
        }
        return null
    }
}