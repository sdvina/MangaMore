package org.sdvina.mangamore.repository

import android.content.Context

interface AppContainer {
    val accountRepository: AccountRepository
    val comicRepository: ComicRepository
    val libraryRepository: LibraryRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {

    override val accountRepository: AccountRepository by lazy { AccountRepository() }

    override val comicRepository: ComicRepository by lazy { ComicRepository(context) }

    override val libraryRepository: LibraryRepository by lazy { LibraryRepository(context) }
}