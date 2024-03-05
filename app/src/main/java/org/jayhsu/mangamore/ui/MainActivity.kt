package org.jayhsu.mangamore.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.jayhsu.mangamore.MangaMoreApplication
import org.jayhsu.mangamore.repository.AppContainer
import org.jayhsu.mangamore.repository.AppContainerImpl
import org.jayhsu.mangamore.ui.theme.MangaMoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as MangaMoreApplication).container
        setContent {
            MangaMoreApp(appContainer)
        }
    }
}

@Composable
fun MangaMoreApp(
    appContainer: AppContainer
) {
    MangaMoreTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val appNavigation = remember(navController){ AppNavigation(navController) }
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    AppDrawer(
                        appNavigation = appNavigation,
                        closeDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            ){
                AppNavGraph(
                    navController = navController,
                    openDrawer = { scope.launch { drawerState.open() } },
                    appNavigation = appNavigation,
                    appContainer = appContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedMoreAppPreview() {
    val appContainer = AppContainerImpl(LocalContext.current)
    MangaMoreTheme {
        MangaMoreApp(appContainer)
    }
}