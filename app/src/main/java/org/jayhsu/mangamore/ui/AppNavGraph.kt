package org.jayhsu.mangamore.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.jayhsu.mangamore.repository.AppContainer
import org.jayhsu.mangamore.ui.comic.ComicViewModel
import org.jayhsu.mangamore.ui.comic.SliderView
import org.jayhsu.mangamore.ui.home.ComicTopScreen
import org.jayhsu.mangamore.ui.home.ComicTopType
import org.jayhsu.mangamore.ui.home.HomeViewModel
import org.jayhsu.mangamore.ui.library.ComicListScreen
import org.jayhsu.mangamore.ui.library.FolderListScreen
import org.jayhsu.mangamore.ui.library.LibraryViewModel

object AppDestinations {
    const val HOME = "home"
    const val LIBRARY = "library"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val COMIC_VIEWER = "comicViewer"
    const val COMIC_LIST = "comicList"
}

@Composable
fun AppNavGraph (
    navController: NavHostController,
    openDrawer: ()  -> Unit,
    appNavigation: AppNavigation,
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME,
        modifier = Modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(750))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(750))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(750))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(750))
        }
    ){
        composable(AppDestinations.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(appContainer.libraryRepository)
            )
            ComicTopScreen(
                appNavigation = appNavigation,
                openDrawer = openDrawer,
                readingComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.READING),
                unreadComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.UNREAD),
                readComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.READ),
                favoritedComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.FAVORITED)
            )
        }
        composable(AppDestinations.LIBRARY) {
            val libraryViewModel: LibraryViewModel = viewModel(
                factory = LibraryViewModel.provideFactory(appContainer.libraryRepository)
            )
            FolderListScreen(
                appNavigation = appNavigation,
                openDrawer = openDrawer,
                folderItemsFlow = libraryViewModel.getFolderItemsFlow() ,
                onFolderItemSelected = { libraryViewModel.selectFolderItem(it) },
                addFolder = { libraryViewModel.addFolder(it) }
            )
        }
        composable(AppDestinations.COMIC_LIST){
            val libraryViewModel: LibraryViewModel = viewModel(
                factory = LibraryViewModel.provideFactory(appContainer.libraryRepository)
            )
            ComicListScreen(
                appNavigation = appNavigation,
                viewModelState = libraryViewModel.state,
                comicItemsFlow = libraryViewModel.searchComicItemsFlow(),
                onToggleFavorite = {}
            )
        }
        composable(
            route = AppDestinations.COMIC_VIEWER + "/{comicId}",
            arguments = listOf(navArgument("comicId"){
                type = NavType.LongType
            })
        ){
            val comicId = it.arguments?.getLong("comicId")
            val comicViewModel: ComicViewModel = viewModel(
                factory = ComicViewModel.provideFactory(appContainer.comicRepository)
            )

            SliderView(
                appNavigation = appNavigation,
                getComicItem =  { comicViewModel.getComicItem(comicId!!)},
                comicItemDetail = comicViewModel.getComicItemDetail(comicId!!),
                getComicPageItemData =  {comicPageItem -> comicViewModel.getComicPageData(comicPageItem)},
            )
        }
        composable(AppDestinations.SETTINGS) {
            // TODO
        }
        composable(AppDestinations.ABOUT) {
            // TODO
        }
    }
}