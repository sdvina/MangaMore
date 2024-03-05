package org.jayhsu.mangamore

import android.app.Application
import com.tencent.mmkv.MMKV
import org.jayhsu.mangamore.data.local.AppDatabaseHelper
import org.jayhsu.mangamore.repository.AppContainer
import org.jayhsu.mangamore.repository.AppContainerImpl
import timber.log.Timber

class MangaMoreApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        MMKV.initialize(this)
        AppDatabaseHelper.onCreate(this)
    }
}