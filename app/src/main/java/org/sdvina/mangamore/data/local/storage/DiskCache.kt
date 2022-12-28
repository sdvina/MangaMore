package org.sdvina.mangamore.data.local.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import org.sdvina.mangamore.data.enums.ComicType
import org.sdvina.mangamore.data.enums.DiskCacheType
import org.sdvina.mangamore.data.local.storage.file.ArchiveFileReader
import org.sdvina.mangamore.data.local.storage.file.ComicValidator
import org.sdvina.mangamore.data.model.ComicItemDetail
import org.sdvina.mangamore.data.model.ComicPageItem
import java.io.File
import java.io.OutputStream

object  DiskCache {

    fun saveFile(context: Context, filename: String, cacheType: DiskCacheType, tofile: File) {
        val file = context.getDir(cacheType.path + filename, Context.MODE_PRIVATE)
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            //it.bufferedWriter(tofile.outputStream().)
        }
    }

    fun saveBitmap(path: String, bitmap: Bitmap) {
        try {
            val file = File(path)
            val fos: OutputStream = file.outputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // 遍历子级目录 1 判断文件是支持的压缩包且里面是受支持的图片 2 解压第一张图片作为 封面  改名 uuid 3 缓存 MMK 4, 文件夹存储数据库， 文件存储数据库

    fun extractComicItemDetails(context: Context, file: DocumentFile): ComicItemDetail?{
        if(!file.isDirectory){
            file.name?.let {
                ComicValidator.validateComicType(it)?.let { comicType ->
                    when(comicType){
                        ComicType.ZIP -> return   ArchiveFileReader.readZipFile(context, file)
                    }
                }
            }
        }
        return null
    }

    fun getImageUri(context: Context, archiveFileUri: Uri, comicPageItem: ComicPageItem): Uri{
       return  ArchiveFileReader.exctractZipEntryToUri(context, comicPageItem)
    }

/*
    // Request code for selecting a PDF document.
    const val PICK_PDF_FILE = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }
*/

}