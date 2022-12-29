package org.sdvina.mangamore.ui.comic

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.model.ComicItem
import org.sdvina.mangamore.data.model.ComicItemDetail
import org.sdvina.mangamore.data.model.ComicPageItem
import org.sdvina.mangamore.ui.AppNavigation
import org.sdvina.mangamore.ui.components.MoreActionsButton
import org.sdvina.mangamore.ui.components.imageviewer.ImageGallery
import org.sdvina.mangamore.ui.extentions.generateDominantColorState

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPagerApi
@Composable
fun SliderView(
    appNavigation: AppNavigation,
    getComicItem: suspend () -> ComicItem,
    comicItemDetail: ComicItemDetail,
    getComicPageItemData: (ComicPageItem) -> ByteArray,
) {
    val tapState = remember { mutableStateOf(false) }
    val systemUiController: SystemUiController = rememberSystemUiController()
    //systemUiController.isSystemBarsVisible = tapState.value  // 隐藏状态栏 底部导航
    systemUiController.isNavigationBarVisible = tapState.value
    val statusbarColor = Color.Transparent
    systemUiController.run {
        setStatusBarColor(statusbarColor, false)
        setSystemBarsColor(statusbarColor, false)
        setNavigationBarColor(statusbarColor, false)
    }
    val pagerState = rememberPagerState()
    val page = remember { mutableStateOf(0) }

    Scaffold(
        topBar = { if(tapState.value){
            ComicTopBar(
            onBack = appNavigation.navigationToBack,
            comicItemDetail = comicItemDetail
        ) } },
        bottomBar = { if(tapState.value){ ComicBottomBar(

        ) } },
        snackbarHost = {  }
    ){  innerPadding ->
        Modifier.padding(innerPadding)
        val context = LocalContext.current
        val defaultBitmap = ImageBitmap.imageResource(context.resources, R.drawable.comic_header).asAndroidBitmap()
        val currentBitmap = remember { mutableStateOf(defaultBitmap) }
        val swatch = currentBitmap.value.generateDominantColorState()
        val dominantColors = listOf(Color(swatch.rgb), Color.Black)
        val comicPageItems = comicItemDetail.comicPageItems!!

        ImageGallery(
            modifier = Modifier.fillMaxSize(),
            count = comicPageItems.size,
            state = pagerState,
            imageLoader = { index ->
                page.value = index
                val comicPageItem = comicPageItems[index]
                val imageRequest = ImageRequest.Builder(LocalContext.current)
                    .data(when(comicPageItem.uri != null){
                            true -> comicPageItem.uri
                            false -> when(comicPageItem.data != null){
                                true -> comicPageItem.data
                                else -> getComicPageItemData(comicPageItem)
                            }
                    })
                    .size(Size.ORIGINAL)
                    .build()
                // 获取图片的初始大小
                rememberAsyncImagePainter(imageRequest)
            },
            onTap = {tapState.value = !tapState.value }
        )
    }
}

@Composable
private fun loadComicItemFromDb(getComicItem: suspend () -> ComicItem): State<Result<ComicItem>> {
    return produceState<Result<ComicItem>>(initialValue = Result.Loading, getComicItem) {
        val comicItem = getComicItem()
        value = Result.Success(comicItem)
    }
}

sealed class Result<ComicItem> {
    object Loading : Result<ComicItem>()
    class Success(val comicItem: ComicItem) : Result<ComicItem>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicTopBar(
    onBack: () -> Unit,
    comicItemDetail: ComicItemDetail
) {
    SmallTopAppBar (
        title = {
                Text(
                    text = comicItemDetail.comicItemName,
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .padding(4.dp),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            },
        modifier = Modifier.statusBarsPadding()
        ,
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
            MoreActionsButton(){}
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
        scrollBehavior = null
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ComicBottomBar(
) {
    BottomAppBar (
        modifier = Modifier
            .navigationBarsPadding()
            .fillMaxWidth()
            .height(120.dp),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 4.dp,
        contentPadding = PaddingValues(8.dp),
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            var sliderState by remember { mutableStateOf(0f) }
            Slider(
                value = sliderState/*comicItem.pageNo.toFloat()*/,
                onValueChange = { newValue -> sliderState = newValue },
                modifier = Modifier.padding(4.dp),
/*                valueRange = object: ClosedFloatingPointRange<Float>{
                    override fun lessThanOrEquals(a: Float, b: Float): Boolean {
                        return b - a >= 0
                    }

                    override val start: Float
                        get() = 0f

                    override val endInclusive: Float
                        get() = *//*comicItem.pageTotal.toFloat()*//* 100f
                },*/
                onValueChangeFinished = {
                    // 更新 进度
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
            }
        }
    }
}