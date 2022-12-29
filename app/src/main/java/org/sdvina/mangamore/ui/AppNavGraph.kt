package org.sdvina.mangamore.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import org.sdvina.mangamore.repository.AppContainer
import org.sdvina.mangamore.ui.comic.ComicViewModel
import org.sdvina.mangamore.ui.comic.SliderView
import org.sdvina.mangamore.ui.home.ComicTopScreen
import org.sdvina.mangamore.ui.home.ComicTopType
import org.sdvina.mangamore.ui.home.HomeViewModel

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val LIBRARY_ROUTE = "library"
    const val SETTINGS_ROUTE = "settings"
    const val ABOUT_ROUTE = "about"
    const val COMIC_VIEWER_ROUTE = "comicViewer"
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun AppNavGraph (
    navController: NavHostController,
    openDrawer: ()  -> Unit,
    appNavigation: AppNavigation,
    appContainer: AppContainer
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = AppDestinations.HOME_ROUTE,
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
            composable(AppDestinations.HOME_ROUTE) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.libraryRepository)
                )
                ComicTopScreen(
                    openDrawer = openDrawer,
                    readingComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.READING),
                    unreadComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.UNREAD),
                    readComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.READ),
                    favoritedComicItemsFlow = homeViewModel.getComicItemsFlow(ComicTopType.FAVORITED),
                    appNavigation = appNavigation
                )
            }
/*            composable(AppDestinations.LIBRARY_ROUTE) {
                val libraryViewModel: LibraryViewModel = viewModel(
                    factory = LibraryViewModel.provideFactory(appContainer)
                )
                LibraryScreen(
                    navController = navController,
                    viewModel = LibraryViewModel
                )
            }*/
            composable(
                route = AppDestinations.COMIC_VIEWER_ROUTE +"/{comicId}",
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
            composable(AppDestinations.SETTINGS_ROUTE) {
                // TODO
            }
            composable(AppDestinations.ABOUT_ROUTE) {
                // TODO
            }
        }
    }