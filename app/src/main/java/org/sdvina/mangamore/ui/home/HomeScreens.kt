package org.sdvina.mangamore.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.itemsIndexed
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.ui.AppNavigation
import org.sdvina.mangamore.ui.components.MoreActionsButton
import org.sdvina.mangamore.ui.components.verticalgrid.VerticalGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicTopScreen(
    openDrawer: () -> Unit,
    readingComicItemsFlow: Flow<PagingData<ComicItem>>,
    unreadComicItemsFlow: Flow<PagingData<ComicItem>>,
    readComicItemsFlow: Flow<PagingData<ComicItem>>,
    favoritedComicItemsFlow: Flow<PagingData<ComicItem>>,
    appNavigation: AppNavigation,
    modifier: Modifier = Modifier
) {
    val readingComicItems = readingComicItemsFlow.collectAsLazyPagingItems()
    val unreadComicItems = unreadComicItemsFlow.collectAsLazyPagingItems()
    val readComicItems = readComicItemsFlow.collectAsLazyPagingItems()
    val favoritedComicItems = favoritedComicItemsFlow.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            HomeTopAppBar(
                openDrawer = openDrawer
            )
        },
        modifier = modifier.statusBarsPadding()
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val scrollState = rememberScrollState(0)
        Box(modifier = contentModifier.fillMaxSize()){
            Column(modifier = modifier
                .padding(8.dp)
                .verticalScroll(state = scrollState)) {
                if(unreadComicItems.itemCount>0){
                    ComicGridSection(unreadComicItems.itemSnapshotList.items, appNavigation)
                    ComicItemsTitle(
                        title = stringResource(R.string.reading),
                        itemCount = readingComicItems.itemCount.toString(),
                        modifier = Modifier
                    )
                    ComicLane(readingComicItems, appNavigation)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if(unreadComicItems.itemCount>0){
                    ComicItemsTitle(
                        title = stringResource(R.string.unread),
                        itemCount = readingComicItems.itemCount.toString(),
                        modifier = Modifier
                    )
                    ComicLane(unreadComicItems, appNavigation)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if(favoritedComicItems.itemCount>0){
                    ComicItemsTitle(
                        title = stringResource(R.string.favorited),
                        itemCount = favoritedComicItems.itemCount.toString(),
                        modifier = Modifier
                    )
                    ComicLane(favoritedComicItems, appNavigation)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if(readComicItems.itemCount>0){
                    ComicItemsTitle(
                        title = stringResource(R.string.read),
                        itemCount = readComicItems.itemCount.toString(),
                        modifier = Modifier
                    )
                    ComicLane(readComicItems, appNavigation)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ComicItemsTitle(title: String, itemCount: String,  modifier: Modifier = Modifier) {
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
fun ComicGridSection(items: List<ComicItem>, appNavigation: AppNavigation) {
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

private fun Modifier.notifyInput(block: () -> Unit): Modifier =
    composed {
        val blockState = rememberUpdatedState(block)
        pointerInput(Unit) {
            while (currentCoroutineContext().isActive) {
                awaitPointerEventScope {
                    awaitPointerEvent(PointerEventPass.Initial)
                    blockState.value()
                }
            }
        }
    }

@Composable
fun ComicLane(pagingComicItems: LazyPagingItems<ComicItem>, appNavigation: AppNavigation, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier) {
        itemsIndexed(pagingComicItems) { index, comicItem ->
            comicItem?.let { ComicLaneItem(comicItem = comicItem, appNavigation = appNavigation) }
        }
    }
}

@Preview
@Composable
fun HomeTopBarPreview(){
    HomeTopAppBar {}
}