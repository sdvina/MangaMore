package org.sdvina.mangamore.ui.library

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.data.model.FolderItem
import org.sdvina.mangamore.ui.AppNavigation
import org.sdvina.mangamore.ui.components.MoreActionsButton
import org.sdvina.mangamore.ui.theme.MangaMoreTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderListScreen(
    appNavigation: AppNavigation,
    openDrawer: () -> Unit,
    folderItemsFlow: Flow<PagingData<FolderItem>>,
    onFolderItemSelected: (folderItem: FolderItem) -> Unit,
    addFolder:(Uri?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree(), onResult = addFolder)
    Scaffold(
        floatingActionButton = {
           FloatingActionButton(onClick = { launcher.launch(null) }) {
               Icon(Icons.Filled.Add, null)
           }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LibraryTopAppBar(
                openDrawer = openDrawer
            )
        },
        modifier = modifier.statusBarsPadding()
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val folderItems = folderItemsFlow.collectAsLazyPagingItems()
        FolderItemList(
            appNavigation = appNavigation,
            folderItems = folderItems,
            onFolderItemSelected = onFolderItemSelected,
            modifier = contentModifier
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun ComicListScreen(
    appNavigation: AppNavigation,
    viewModelState: StateFlow<LibraryViewModelState>,
    comicItemsFlow: Flow<PagingData<ComicItem>>,
    onToggleFavorite: (comicId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewState by viewModelState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ComicListTopAppBar(
                onBack = appNavigation.navigationToBack,
                folderName = when(viewState.folderItem == null){
                    true -> ""
                    false -> viewState.folderItem!!.name
                }
            )
        },
        modifier = modifier.statusBarsPadding()
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        val comicItems = comicItemsFlow.collectAsLazyPagingItems()
        ComicItemList(
            appNavigation = appNavigation,
            comicItems = comicItems,
            onToggleFavorite =onToggleFavorite,
            modifier = contentModifier
        )
    }
}


@Composable
fun FolderItemList(
    appNavigation: AppNavigation,
    folderItems: LazyPagingItems<FolderItem>,
    onFolderItemSelected: (folderItem: FolderItem) -> Unit,
    modifier: Modifier = Modifier ){
    LazyColumn(
        modifier = modifier
        .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(0.dp),
        state = rememberLazyListState()){
        itemsIndexed(folderItems){ _, folderItem ->
            folderItem?.let {
                FolderItemCard(appNavigation, folderItem, onFolderItemSelected)
            }
        }
    }
}



@Composable
fun ComicItemList(
    appNavigation: AppNavigation,
    comicItems: LazyPagingItems<ComicItem>,
    onToggleFavorite: (comicId: Long) -> Unit,
    modifier: Modifier
) {
    LazyColumn( modifier = modifier
        .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(0.dp),
        state = rememberLazyListState()){
        itemsIndexed(comicItems){_, comicItem ->
            comicItem?.let {
                ComicItemCard(appNavigation, comicItem,  {  comicItems.refresh() }, onToggleFavorite)
            }
        }
    }
}

/**
 * TopAppBar for the Library screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryTopAppBar(
    openDrawer: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                text = stringResource(R.string.library))
        },
        modifier = Modifier.defaultMinSize(),
        navigationIcon = {
            IconButton(onClick = openDrawer) {
                Icon(
                    imageVector = Icons.Filled.Dehaze,
                    contentDescription = stringResource(R.string.cd_open_navigation_drawer),
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
            MoreActionsButton {}
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(), // TODO
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComicListTopAppBar(
    onBack: () -> Unit,
    folderName: String
) {
    TopAppBar(
        title = {
            Text(text = folderName)
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back),
                    tint = MaterialTheme.colorScheme.primary
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
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(), // TODO
    )
}


@Preview("Library screen", device = Devices.PIXEL_C)
@Preview("Library screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_C)
@Preview("Library screen (big font)", fontScale = 1.5f, device = Devices.PIXEL_C)
@Composable
fun PrevLibraryScreen() {
    val libraryViewModel =
    MangaMoreTheme {
      /* LibraryScreen(
           showTopAppBar = true,
           openDrawer = {},
           libraryListLazyListState = rememberLazyListState(),
           folderItemsFlow = {},
           onSelectFolderItem = {},
           onSelectComicItem = {},
           onToggleFavorite = {},
           scaffoldState = rememberScaffoldState()
        )*/
    }
}