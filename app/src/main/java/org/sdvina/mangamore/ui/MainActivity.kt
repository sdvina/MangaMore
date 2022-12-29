package org.sdvina.mangamore.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.sdvina.mangamore.MangaMoreApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as MangaMoreApplication).container
        setContent {
            MangaMoreApp(appContainer)
        }
    }
}