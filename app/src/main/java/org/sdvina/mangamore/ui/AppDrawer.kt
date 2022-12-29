package org.sdvina.mangamore.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.constant.UrlConstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    appNavigation: AppNavigation,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier) {
        DrawerHeader()
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text(stringResource(R.string.home)) },
            selected = false,
            onClick = {
                appNavigation.navigateToHome()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.LibraryBooks, contentDescription = null) },
            label = { Text(stringResource(R.string.library)) },
            selected = false,
            onClick = {
                appNavigation.navigateToLibrary()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text(stringResource(R.string.settings)) },
            selected = false,
            onClick = {
                appNavigation.navigateToSettings()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
            label = { Text(stringResource(R.string.about)) },
            selected = false,
            onClick = {
                appNavigation.navigateToAbout()
                closeDrawer()
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

    }
}

@Composable
fun DrawerHeader(modifier: Modifier = Modifier) {
    val imageUrl = UrlConstant.BING_IMAGE_DAILY_URL
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .placeholder(R.drawable.comic_header)
            .data(imageUrl)
            .size(360, 240)
            .build(),
        contentDescription = null,
        modifier = modifier.size(360.dp, 240.dp),
        alignment = Alignment.CenterStart,
        contentScale = ContentScale.Crop
    )
}