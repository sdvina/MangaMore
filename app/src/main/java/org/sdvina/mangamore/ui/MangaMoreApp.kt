package org.sdvina.mangamore.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch
import org.sdvina.mangamore.repository.AppContainer
import org.sdvina.mangamore.repository.AppContainerImpl
import org.sdvina.mangamore.ui.theme.MangaMoreTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MangaMoreApp(
    appContainer: AppContainer
) {
    MangaMoreTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val navController = rememberAnimatedNavController()
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