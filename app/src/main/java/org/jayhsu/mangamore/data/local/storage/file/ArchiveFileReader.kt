package org.jayhsu.mangamore.data.local.storage.file

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.rarfile.FileHeader
import net.sf.sevenzipjbinding.*
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.jayhsu.mangamore.data.enums.ComicType
import org.jayhsu.mangamore.data.enums.DiskCacheType
import org.jayhsu.mangamore.data.model.ComicItemDetail
import org.jayhsu.mangamore.data.model.ComicPageItem
import timber.log.Timber
import java.io.*
import java.math.BigDecimal
import java.text.CollationKey
import java.text.Collator
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object ArchiveFileReader {

    private val startWithNumberPattern: Pattern = Pattern.compile("^\\d+")
    private val endWithNumberPattern: Pattern = Pattern.compile("\\d+$")
    private val archiveSeparator: String = File.separator
    private const val BUFFER_SIZE = 4096

    private fun getLastFileName(fullName: String, separator: String): String {
        if (fullName.endsWith(separator))
            return fullName.substring(0, fullName.length - 1)
        if (fullName.contains(separator))
            return fullName.substring(fullName.lastIndexOf(separator) + 1)
        return fullName
    }

    private fun getFileNameFromPath(path: String): String {
        return path.substring(path.lastIndexOf(File.separator) + 1)
    }

    fun readZipFile(context: Context, archiveFile: DocumentFile): ComicItemDetail {
        val appender: MutableList<ComicPageItem> = mutableListOf()
        try { // 默认 UTF-8  否则需要 就要检查 code
            val zips = ZipArchiveInputStream(context.contentResolver.openInputStream(archiveFile.uri))
            do {
                val entry = zips.nextEntry ?: break
                val fullName = entry.name.replace("//".toRegex(), "").replace("\\\\".toRegex(), "")
                val pathLevel = fullName.split(archiveSeparator).toTypedArray().size
                // 展示名
                val originName: String = getLastFileName(fullName, archiveSeparator)
                var childName = pathLevel.toString() + "_" + originName
                if (!entry.isDirectory) {
                    childName = archiveFile.name + "_" + originName
                }
                if (!entry.isDirectory && ComicValidator.validateImageType(childName) != null) {
                    appender.add(
                        ComicPageItem(
                            uri = null,
                            pageName = originName,
                            entryName = entry.name,
                            data = null,
                            comicUri = archiveFile.uri,
                            comicName = archiveFile.name!!,
                        )
                    )
                }
            } while (true)
        } catch (e: IOException) {
            Timber.e("", e.printStackTrace())
        }
        appender.sortWith(sortComparator)
        Timber.i(
            "ComicItemDetail",
            "压缩包：${archiveFile.name}+${archiveFile.uri}+${appender.size}"
        )
        return ComicItemDetail(
            null,
            archiveFile.name!!,
            archiveFile.uri,
            extractZipEntryToUri(context, appender.first()),
            ComicType.ZIP,
            appender
        )
    }

    fun extractZipEntryToUri(context: Context, comicPageItem: ComicPageItem ): Uri{
        val fileDir = context.getDir(DiskCacheType.COMIC.path, Context.MODE_PRIVATE)
        val fileName = comicPageItem.comicName + "_" +comicPageItem.pageName
        try {
            val zips = ZipArchiveInputStream(context.contentResolver.openInputStream(comicPageItem.comicUri))
            FileOutputStream(File(fileDir, fileName)).use { fot ->
                val inByte = ByteArray(BUFFER_SIZE)
                do {
                    val entry = zips.nextEntry?: break
                    if(entry.name.equals(comicPageItem.entryName)) {
                        while(true){
                            val len = zips.read(inByte)
                            if(len == -1) break
                            fot.write(inByte, 0, len)
                        }
                        break
                    }
                } while(true)
                fot.close()
            } } catch (e: IOException) { e.printStackTrace() }
        val uri = Uri.fromFile(File(fileDir, fileName))
        comicPageItem.uri = uri
        return uri
    }

    fun extractZipEntryToByteArray(context: Context, comicPageItem: ComicPageItem): ByteArray?{
        try {
            val zips = ZipArchiveInputStream(context.contentResolver.openInputStream(comicPageItem.comicUri))
            do {
                val entry = zips.nextEntry?: break
                if(entry.name.equals(comicPageItem.entryName)) {
                    val byteArray = IOUtils.toByteArray(zips)
                    comicPageItem.data = byteArray
                    return byteArray
                }
            } while(true)
        } catch (e: IOException) { e.printStackTrace() }
        return null
    }

    private fun getLast2FileName(fullName: String, separator: String, rootName: String): String {
        var fullName = fullName
        if (fullName.endsWith(separator)) {
            fullName = fullName.substring(0, fullName.length - 1)
        }
        // 1.获取剩余部分
        val endIndex = fullName.lastIndexOf(separator)
        val leftPath = fullName.substring(0, if (endIndex == -1) 0 else endIndex)
        return if (leftPath.length > 1) {
            // 2.获取倒数第二个
            getLastFileName(leftPath, separator)
        } else rootName
    }

    fun readLZMAFile(
        fileUri: String,
        context: Context,
        archiveFile: DocumentFile
    ): ComicItemDetail {
        val archiveFileName: String = getFileNameFromPath(fileUri)
        //val sevenZFile2 = SevenZip.openInArchive(ArchiveFormat.LZMA, ByteArrayStream(IOUtils.toByteArray(context.contentResolver.openInputStream(archiveFile.uri)), true))
        //val xzips = XZInputStream(context.contentResolver.openInputStream(archiveFile.uri))
        val appender: MutableList<ComicPageItem> = mutableListOf()
        val sevenZFile = SevenZFile(
            SeekableInMemoryByteChannel(
                IOUtils.toByteArray(
                    context.contentResolver.openInputStream(archiveFile.uri)
                )
            )
        )
        try {
            // 排序
            val entries = sevenZFile.entries
            entries.forEach {
                val entry = it
                val fullName = entry.name.replace("//".toRegex(), "").replace("\\\\".toRegex(), "")
                val level = fullName.split(archiveSeparator).toTypedArray().size
                // 展示名
                val originName = getLastFileName(fullName, archiveSeparator)
                var childName = level.toString() + "_" + originName
                val directory = entry.isDirectory
                if (!directory) {
                    childName = archiveFileName + "_" + originName
                    //entriesToBeExtracted.add(Collections.singletonMap(childName, entry))
                }
                if (!entry.isDirectory && ComicValidator.validateImageType(childName) != null) {
                    appender.add(
                        ComicPageItem(
                            uri = null,
                            pageName = originName,
                            entryName = entry.name,
                            data = null,
                            comicUri = archiveFile.uri,
                            comicName = archiveFile.name!!,
                        )
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        appender.sortWith(sortComparator)
        Timber.i(
            "ComicItemDetail",
            "压缩包：${archiveFile.name}+${archiveFile.uri}+${appender.size}"
        )
        return ComicItemDetail(
            null,
            archiveFile.name!!,
            archiveFile.uri,
            extractLZMAEntryToUri(context, sevenZFile, appender.first()),
            ComicType.ZIP,
            appender
        )
    }

    private fun extractLZMAEntryToUri(context: Context, sevenZFile: SevenZFile, comicPageItem: ComicPageItem): Uri {
        val fileDir = context.getDir(DiskCacheType.COMIC.path, Context.MODE_PRIVATE)
        val fileName = comicPageItem.comicName + "_" + comicPageItem.pageName
        try {
            FileOutputStream(File(fileDir, fileName)).use { fot ->
                val inByte = ByteArray(BUFFER_SIZE)
                do {
                    val entry = sevenZFile.nextEntry ?: break
                    if (entry.name.equals(comicPageItem.entryName)) {
                        while (true) {
                            val len = sevenZFile.getInputStream(entry).read(inByte)
                            if (len == -1) break
                            fot.write(inByte, 0, len)
                        }
                        break
                    }
                } while (true)
                fot.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val uri = Uri.fromFile(File(fileDir, fileName))
        comicPageItem.uri = uri
        return uri
    }

    fun extractLZMAEntryToByteArray(sevenZFile: SevenZFile, comicPageItem: ComicPageItem): ByteArray?{
        try {
            do {
                val entry = sevenZFile.nextEntry ?: break
                if(entry.name.equals(comicPageItem.entryName)) {
                    val byteArray = IOUtils.toByteArray(sevenZFile.getInputStream(entry))
                    comicPageItem.data = byteArray
                    return byteArray
                }
            } while(true)
        } catch (e: IOException) { e.printStackTrace() }
        return null
    }


    private var sortComparator: Comparator<ComicPageItem> = object : Comparator<ComicPageItem> {

        val cmp: Collator = Collator.getInstance(Locale.US)
        override fun compare(p0: ComicPageItem, p1: ComicPageItem): Int {
            // 判断两个对比对象是否是开头包含数字，如果包含数字则获取数字并按数字真正大小进行排序
            val startNum0: BigDecimal? = isStartWithNumber(p0)
            val startNum1: BigDecimal? = isStartWithNumber(p1)

            if((startNum0 != null) && (startNum1 != null)){
                return startNum0.subtract(startNum1).toInt()
            }
            // 判断两个对比对象是否是结尾包含数字，如果包含数字则获取数字并按数字真正大小进行排序
            if(p0.pageName.contains(".")&& p1.pageName.contains(".")){
                val endNum0: BigDecimal? = isEndWithNumber(p0)
                val endNum1: BigDecimal? = isEndWithNumber(p1)

                if((endNum0 != null) && (endNum1 != null)){
                    return endNum0.subtract(endNum1).toInt()
                }
            }
            val c0: CollationKey = cmp.getCollationKey(p0.pageName)
            val c1: CollationKey = cmp.getCollationKey(p1.pageName)
            return cmp.compare(c0.sourceString, c1.sourceString)
        }
    }

    private fun isStartWithNumber(src: ComicPageItem): BigDecimal? {
        val matcher: Matcher = startWithNumberPattern.matcher(src.pageName)
        return if (matcher.find()) {
            BigDecimal(matcher.group())
        } else null
    }

    private fun isEndWithNumber(src: ComicPageItem): BigDecimal? {
        val matcher: Matcher = endWithNumberPattern.matcher(src.pageName.split(".")[0])
        return if (matcher.find()) {
            BigDecimal(matcher.group())
        } else
        return null
    }

/*
    fun unRar(filePath: String) {
        val imgUrls: MutableList<String> = ArrayList()
        val baseUrl: String = BaseUrlFilter.getBaseUrl()
        try{
            val items = getRar4Paths(filePath)
            val archiveFileName: String = fileHandlerService.getFileNameFromPath(filePath)
            val headersToBeExtract: MutableList<Map<String, FileHeaderRar>> = ArrayList()
            for (header in items) {
                val fullName: String = header.getFileNameW()
                val originName = getLastFileName(fullName, File.separator)
                var childName = originName
                val directory: Boolean = header.getDirectory()
                if (!directory) {
                    childName = archiveFileName + "_" + originName
                    headersToBeExtract.add(Collections.singletonMap(childName, header))
                }
                val parentName: String = getLast2FileName(fullName, File.separator, archiveFileName)
                val type: FileType = FileType.typeFromUrl(childName)
                if (type.equals(FileType.PICTURE)) {
                    imgUrls.add(baseUrl + childName)
                }
                val node =
                    FileNode(originName, childName, parentName, ArrayList(), directory, fileKey)
                addNodes(appender, parentName, node)
                appender[childName] = node
            }
            fileHandlerService.putImgCache(fileKey, imgUrls)
            executors.submit(RarExtractorWorker(headersToBeExtract, filePath))
            return ObjectMapper().writeValueAsString(appender[""])
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
*/

/*    fun getRar4Paths(paths: String): List<FileHeaderRar>? {
        var randomAccessFile: RandomAccessFile? = null
        var inArchive: IInArchive? = null
        var itemPath: List<FileHeaderRar>? = null
        try {
            randomAccessFile = RandomAccessFile(paths, "r")
            inArchive = SevenZip.openInArchive(null, RandomAccessFileInStream(randomAccessFile))
            val folderName = paths.substring(paths.lastIndexOf(File.separator) + 1)
            val extractPath = paths.substring(0, paths.lastIndexOf(folderName))
            inArchive.extract(
                null,
                false,
                ExtractCallback(inArchive, extractPath, folderName + "_")
            )
            val simpleInArchive = inArchive.simpleInterface
            itemPath = Arrays.stream(simpleInArchive.archiveItems)
                .map { o ->
                    try {
                        return@map FileHeaderRar(o.path, o.isFolder)
                    } catch (e: SevenZipException) {
                        e.printStackTrace()
                    }
                    null
                }
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparing(FileHeaderRar::getFileName))
                .collect(Collectors.toList()) as List<FileHeaderRar>

        } catch (e: Exception) {
            System.err.println("Error occurs: $e")
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close()
                } catch (e: SevenZipException) {
                    System.err.println("Error closing archive: $e")
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close()
                } catch (e: IOException) {
                    System.err.println("Error closing file: $e")
                }
            }
        }
        return itemPath
    }*/


    internal class RarExtractorWorker : Runnable {
        private val headersToBeExtracted: List<Map<String, FileHeader>>?
        private val headersToBeExtract: List<Map<String, FileHeaderRar>>?
        private val archive: Archive?

        /**
         * 用以删除源文件
         */
        private val filePath: String

        constructor(
            headersToBeExtracted: List<Map<String, FileHeader>>?,
            archive: Archive?,
            filePath: String
        ) {
            this.headersToBeExtracted = headersToBeExtracted
            this.archive = archive
            this.filePath = filePath
            headersToBeExtract = null
        }

        constructor(
            headersToBeExtract: List<Map<String, FileHeaderRar>>?, filePath: String
        ) {
            this.headersToBeExtract = headersToBeExtract
            this.filePath = filePath
            archive = null
            headersToBeExtracted = null
        }

        override fun run() {
            for (entryMap in headersToBeExtracted!!) {
                val childName = entryMap.keys.iterator().next()
                extractRarFile(childName, entryMap.values.iterator().next(), archive)
            }
            try {
                archive!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun extractRarFile(childName: String, header: FileHeader, archive: Archive?) {
            val outPath: String = "fileDir$childName"
            try {
                FileOutputStream(outPath).use { ot -> archive!!.extractFile(header, ot) }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: RarException) {
                e.printStackTrace()
            }
        }
    }


    internal class FileHeaderRar(var fileName: String, var directory: Boolean)

    private class ExtractCallback(inArchive: IInArchive, extractPath: String, folderName: String) :
        IArchiveExtractCallback {
        private val inArchive: IInArchive
        private val extractPath: String
        private val folderName: String
        override fun setTotal(total: Long) {}
        override fun setCompleted(complete: Long) {}

        @Throws(SevenZipException::class)
        override fun getStream(index: Int, extractAskMode: ExtractAskMode): ISequentialOutStream {
            val filePath = inArchive.getStringProperty(index, PropID.PATH)
            val real = folderName + filePath.substring(filePath.lastIndexOf(File.separator) + 1)
            val f = File(extractPath + real)
            f.delete()
            return ISequentialOutStream { data: ByteArray ->
                var fos: FileOutputStream? = null
                try {
                    val path = File(extractPath + real)
                    if (!path.parentFile.exists()) {
                        path.parentFile.mkdirs()
                    }
                    if (!path.exists()) {
                        path.createNewFile()
                    }
                    fos = FileOutputStream(path, true)
                    fos.write(data)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (fos != null) {
                            fos.flush()
                            fos.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                data.size
            }
        }

        override fun prepareOperation(extractAskMode: ExtractAskMode) {}
        override fun setOperationResult(extractOperationResult: ExtractOperationResult) {}

        init {
            var path = extractPath
            this.inArchive = inArchive
            if (!path.endsWith("/") && !path.endsWith("\\")) {
                path += File.separator
            }
            this.extractPath = path
            this.folderName = folderName
        }
    }
}