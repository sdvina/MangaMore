package org.jayhsu.mangamore.data.local.storage

import com.github.junrar.Archive
import com.github.junrar.exception.RarException
import com.github.junrar.rarfile.FileHeader
import net.sf.sevenzipjbinding.*
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object TempReader {
    private val archiveSeparator: String = File.separator
    val fileDir: String = ""
    private val executors: ExecutorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    private fun getLastFileName(fullName: String, seperator: String): String {
        if (fullName.endsWith(seperator))
            return fullName.substring(0, fullName.length - 1)
        if (fullName.contains(seperator))
            return fullName.substring(fullName.lastIndexOf(seperator) + 1)
        return fullName
    }

    private fun getFileNameFromPath(path: String): String {
        return path.substring(path.lastIndexOf(File.separator) + 1)
    }

    fun readZipFile(fileUri: String): MutableList<Map<String, ZipArchiveEntry>>{
        val archiveFileName: String = getFileNameFromPath(fileUri)
        val entriesToBeExtracted: MutableList<Map<String, ZipArchiveEntry>> = LinkedList()
        try { // 默认 UTF-8  否则需要 就要检查 code
            val entries: Enumeration<ZipArchiveEntry> = ZipFile(fileUri).entries
            while (entries.hasMoreElements()) {
                val entry: ZipArchiveEntry = entries.nextElement()
                val fullName = entry.name.replace("//".toRegex(), "").replace("\\\\".toRegex(), "")
                val level = fullName.split(archiveSeparator).toTypedArray().size
                // 展示名
                val originName: String = getLastFileName(fullName, archiveSeparator)
                var childName = level.toString() + "_" + originName
                if (!entry.isDirectory) {
                    childName = archiveFileName + "_" + originName
                    entriesToBeExtracted.add(Collections.singletonMap(childName, entry))
                }
                //添加图片文件到图片列表
            }
            // 开启新的线程处理文件解压
            //executors.submit(ZipExtractor(entriesToBeExtracted, zipFile, fileUri))
        } catch (e: IOException) { e.printStackTrace() }
        return entriesToBeExtracted
    }

    fun read7zFile(fileUri: String){
        val archiveFileName: String = getFileNameFromPath(fileUri)
        try {
            val zipFile = SevenZFile(File(fileUri))
            // 排序
            val entries = zipFile.entries
            val entriesToBeExtracted: MutableList<Map<String, SevenZArchiveEntry>> = ArrayList()
            entries.forEach{
                val entry = it
                val fullName = entry.name.replace("//".toRegex(), "").replace("\\\\".toRegex(), "")
                val level = fullName.split(archiveSeparator).toTypedArray().size
                // 展示名
                val originName = getLastFileName(fullName, archiveSeparator)
                var childName = level.toString() + "_" + originName
                val directory = entry.isDirectory
                if (!directory) {
                    childName = archiveFileName + "_" + originName
                    entriesToBeExtracted.add(Collections.singletonMap(childName, entry))
                }
                //添加图片文件到图片列表
            }
            // 开启新的线程处理文件解压
            executors.submit(SevenZExtractorWorker(entriesToBeExtracted, fileUri))
        } catch (e: IOException) { e.printStackTrace() }
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

    fun getRar4Paths(paths: String): List<FileHeaderRar>? {
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
    }
*/


    internal class ZipExtractor(
        private val entriesToBeExtracted: List<Map<String, ZipArchiveEntry?>>,
        private val zipFile: ZipFile,
        val filePath: String
    ) : Runnable {
        override fun run() {
            for (entryMap in entriesToBeExtracted) {
                val childName = entryMap.keys.iterator().next()
                val entry = entryMap.values.iterator().next()
                try { extractZipFile(childName, zipFile.getInputStream(entry)) } catch (e: IOException) { e.printStackTrace() }
            }
            try { zipFile.close() } catch (e: IOException) { e.printStackTrace() }
           // TODO KkFileUtils.deleteFileByPath(filePath)
        }

        private fun extractZipFile(childName: String, zipFile: InputStream) {
            val outPath = "fileDir$childName"
            try { FileOutputStream(outPath).use { fot ->
                    val inByte = ByteArray(1024)
                    var len: Int
                    while (-1 != zipFile.read(inByte).also { len = it }) {
                        fot.write(inByte, 0, len)
                    }
                }
            } catch (e: IOException) { e.printStackTrace() }
        }
    }

    internal class SevenZExtractorWorker(
        private val entriesToBeExtracted: List<Map<String, SevenZArchiveEntry>>,
        private val filePath: String
    ) : Runnable {
        override fun run() {
            try {
                val sevenZFile = SevenZFile(File(filePath))
                var entry = sevenZFile.nextEntry
                while (entry != null) {
                    if (entry.isDirectory) {
                        entry = sevenZFile.nextEntry
                        continue
                    }
                    var childName = "default_file"
                    var entry1: SevenZArchiveEntry
                    for (entryMap in entriesToBeExtracted) {
                        childName = entryMap.keys.iterator().next()
                        entry1 = entryMap.values.iterator().next()
                        if (entry.name == entry1.name) {
                            break
                        }
                    }
                    val out: FileOutputStream = FileOutputStream("fileDir" + childName)
                    val content = ByteArray(entry.size.toInt())
                    sevenZFile.read(content, 0, content.size)
                    out.write(content)
                    out.close()
                    entry = sevenZFile.nextEntry
                }
                sevenZFile.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


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
            val outPath: String = "fileDir" + childName
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
            var extractPath = extractPath
            this.inArchive = inArchive
            if (!extractPath.endsWith("/") && !extractPath.endsWith("\\")) {
                extractPath += File.separator
            }
            this.extractPath = extractPath
            this.folderName = folderName
        }
    }

}