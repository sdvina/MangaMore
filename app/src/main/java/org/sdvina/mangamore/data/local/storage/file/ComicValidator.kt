package org.sdvina.mangamore.data.local.storage.file

import org.sdvina.mangamore.data.enums.ComicType
import org.sdvina.mangamore.data.enums.ImageType

object ComicValidator {
    // 受支持的压缩格式
    fun validateComicType(fileName: String): ComicType? {
        if (fileName.contains(".")){
            ComicType.values().forEach {
                if(it.suffix.contains(fileName.substring(fileName.lastIndexOf(".") + 1).lowercase()))
                    return it
            }
        }
        return null
    }
    // 受支持的图像格式
    fun validateImageType(fileName: String): ImageType? {
        if (fileName.contains(".")){
            ImageType.values().forEach {
                if(it.suffix.contains(fileName.substring(fileName.lastIndexOf(".") + 1).lowercase()))
                    return it
            }
        }
        return null
    }
}