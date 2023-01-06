package org.sdvina.mangamore.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class AppNavigation (navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(AppDestinations.HOME) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToLibrary: () -> Unit = {
        navController.navigate(AppDestinations.LIBRARY) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigationToComicList: () -> Unit = {
        navController.navigate(AppDestinations.COMIC_LIST) {
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigationToComicViewer: (Long) -> Unit = { comicId ->
        navController.navigate(AppDestinations.COMIC_VIEWER + "/$comicId"){
            launchSingleTop = true
        }
    }
    val navigationToBack: () -> Unit = {
        navController.navigateUp()
    }
    val navigateToSettings: () -> Unit = {
        navController.navigate(AppDestinations.SETTINGS) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToAbout: () -> Unit = {
        navController.navigate(AppDestinations.ABOUT) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}