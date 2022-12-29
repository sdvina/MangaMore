package org.sdvina.mangamore

import ando.file.core.FileOperator
import android.app.Application
import com.tencent.mmkv.MMKV
import org.sdvina.mangamore.data.local.AppDatabaseHelper
import org.sdvina.mangamore.repository.AppContainer
import org.sdvina.mangamore.repository.AppContainerImpl

class MangaMoreApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        FileOperator.init(this, BuildConfig.DEBUG)

        MMKV.initialize(this)
        AppDatabaseHelper.onCreate(this)
    }
}