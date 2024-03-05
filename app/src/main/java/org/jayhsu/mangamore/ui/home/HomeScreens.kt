package org.jayhsu.mangamore.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import org.jayhsu.mangamore.R
import org.jayhsu.mangamore.data.model.ComicItem
import org.jayhsu.mangamore.ui.AppNavigation
import org.jayhsu.mangamore.ui.components.MoreActionsButton
import org.jayhsu.mangamore.ui.components.verticalgrid.VerticalGrid

@Composable
fun ComicTopScreen(
    appNavigation: AppNavigation,
    openDrawer: () -> Unit,
    readingComicItemsFlow: Flow<PagingData<ComicItem>>,
    unreadComicItemsFlow: Flow<PagingData<ComicItem>>,
    readComicItemsFlow: Flow<PagingData<ComicItem>>,
    favoritedComicItemsFlow: Flow<PagingData<ComicItem>>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            HomeTopAppBar(openDrawer = openDrawer)
        },
        modifier = modifier.statusBarsPadding()
    ) { innerPadding ->
        val readingComicItems = readingComicItemsFlow.collectAsLazyPagingItems()
        val unreadComicItems = unreadComicItemsFlow.collectAsLazyPagingItems()
        val readComicItems = readComicItemsFlow.collectAsLazyPagingItems()
        val favoritedComicItems = favoritedComicItemsFlow.collectAsLazyPagingItems()
        val scrollState = rememberScrollState(0)
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Column(modifier = modifier
                .padding(8.dp)
                .verticalScroll(state = scrollState)) {
                if(readingComicItems.itemCount > 0){
                    ComicGridSection(appNavigation, unreadComicItems.itemSnapshotList.items)
                    ComicItemsTitle(
                        title = stringResource(R.string.reading),
                        itemCount = readingComicItems.itemCount.toString()
                    )
                    ComicLane(appNavigation, readingComicItems)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if(unreadComicItems.itemCount > 0){
                    ComicItemsTitle(
                        title = stringResource(R.string.unread),
                        itemCount = readingComicItems.itemCount.toString()
                    )
                    ComicLane(appNavigation, unreadComicItems)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if(favoritedComicItems.itemCount > 0){
                    ComicItemsTitle(
                        title = stringResource(R.string.favorited),
                        itemCount = favoritedComicItems.itemCount.toString()
                    )
                    ComicLane(appNavigation, favoritedComicItems)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if(readComicItems.itemCount > 0){
                    ComicItemsTitle(
                        title = stringResource(R.string.read),
                        itemCount = readComicItems.itemCount.toString()
                    )
                    ComicLane(appNavigation, readComicItems)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


        }
    }
}

@Composable
fun ComicItemsTitle(
    title: String,
    itemCount: String,
    modifier: Modifier = Modifier
) {
    Row {
        Text(
            text = title,
            style = typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .padding(start = 8.dp, end = 4.dp, bottom = 8.dp, top = 24.dp)
                .semantics { heading() }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = String.format(stringResource(id = R.string.comic_count), itemCount),
            style = typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier
                .padding(start = 8.dp, end = 4.dp, bottom = 8.dp, top = 24.dp)
                .semantics { heading() }
        )
    }
}

@Composable
fun ComicGridSection(
    appNavigation: AppNavigation,
    items: List<ComicItem>
) {
    ComicItemsTitle(
        title = stringResource(R.string.top_reading),
        itemCount = (if(items.size<6) items.size else 6).toString(),
        modifier = Modifier
    )
    VerticalGrid {
        remember { items }.take(6).forEach {
            ComicGridItem(comicItem = it, appNavigation = appNavigation)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Dehaze,
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Open search */ }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.cd_search)
                )
            }
            MoreActionsButton {
                DropdownMenuItem(
                    text = { Text("") },
                    onClick = { /* Handle! */ },
                    leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) }
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(), // TODO
    )
}


@Composable
fun ComicLane(
    appNavigation: AppNavigation,
    pagingComicItems: LazyPagingItems<ComicItem>,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        items(count = pagingComicItems.itemCount) { index->
            pagingComicItems[index]?.let {
                ComicLaneItem(
                    comicItem = it,
                    appNavigation = appNavigation
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeTopBarPreview(){
    HomeTopAppBar {}
}