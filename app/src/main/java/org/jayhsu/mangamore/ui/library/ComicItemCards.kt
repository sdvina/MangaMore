package org.jayhsu.mangamore.ui.library

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jayhsu.mangamore.data.enums.ReadStatus
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.jayhsu.mangamore.R
import org.jayhsu.mangamore.data.model.ComicItem
import org.jayhsu.mangamore.ui.AppNavigation
import org.jayhsu.mangamore.ui.components.BookmarkButton
import org.jayhsu.mangamore.ui.theme.MangaMoreTheme

@Composable
fun ComicItemImage(comicItem: ComicItem, modifier: Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(comicItem.thumbnailUri)
            .size(120, 140)
            .build(),
        contentDescription = null,
        alignment = Alignment.TopCenter,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(120.dp, 140.dp)
    )
}

@Composable
fun ComicItemName(comicItem: ComicItem, modifier: Modifier) {
    Text(
        text= comicItem.name,
        maxLines = 1,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Left,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(4.dp)
    )
}

@Composable
fun ReadProgress(comicItem: ComicItem, modifier: Modifier) {
    LinearProgressIndicator(
        progress = { (comicItem.pageNo.toFloat()).div(comicItem.pageTotal.toFloat()) },
        modifier = modifier
        .padding(4.dp),
    )
}

@Composable
fun ComicItemType(comicItem: ComicItem, modifier: Modifier) {
    Text(
        text= stringResource(id = comicItem.type.resid),
        modifier = modifier.wrapContentSize(),
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun ReadStatus(comicItem: ComicItem, modifier: Modifier) {
    Text(
        text = when (comicItem.pageNo) {
            0 -> stringResource(id = ReadStatus.UNREAD.resid)
            comicItem.pageTotal -> stringResource(id = ReadStatus.READ.resid)
            else -> stringResource(id = ReadStatus.READING.resid)
        },
        modifier = modifier.wrapContentSize(),
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun ComicItemCard(
    appNavigation: AppNavigation,
    comicItem: ComicItem,
    refresh: () -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    val bookmarkAction = stringResource(if (comicItem.favorited) R.string.cd_unbookmark else R.string.cd_bookmark)
    var openDialog by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clickable(onClick = {
                appNavigation.navigationToComicViewer(comicItem.comicId)
            })
    ) {
        ConstraintLayout( modifier = Modifier.fillMaxWidth()){
            val ( comicImage, favorited, moreOptions,  comicName, readProgress, comicType, readStatus) = createRefs()
            ComicItemImage(comicItem, Modifier.constrainAs(comicImage){
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            })
            BookmarkButton(
                isBookmarked = comicItem.favorited,
                onClick = { onToggleFavorite(comicItem.comicId) },
                // Remove button semantics so action can be handled at row level
                modifier = Modifier
                    .constrainAs(favorited) {
                        top.linkTo(parent.top, 4.dp)
                        end.linkTo(moreOptions.start, 8.dp)
                        bottom.linkTo(comicName.top, 4.dp)
                    }
                    .clearAndSetSemantics {}
            )
            CompositionLocalProvider() { // TODO LocalContentAlpha
                IconButton(
                    onClick = { openDialog = true },
                    modifier = Modifier.constrainAs(moreOptions){
                        end.linkTo(parent.end, 4.dp)
                        top.linkTo(parent.top, 4.dp)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.cd_morevert)
                    )
                }
            }
            ComicItemName(comicItem, modifier = Modifier.constrainAs(comicName){
                start.linkTo(comicImage.end, 4.dp)
                top.linkTo(favorited.bottom, 4.dp)
                end.linkTo(parent.end, 4.dp)
            })
            ReadProgress(comicItem, modifier = Modifier.constrainAs(readProgress){
                top.linkTo(comicName.bottom, 4.dp)
                start.linkTo(comicImage.end, 4.dp)
                end.linkTo(parent.end, 4.dp)
            })
            ComicItemType(comicItem, modifier = Modifier.constrainAs(comicType){
                top.linkTo(readProgress.bottom, 4.dp)
                start.linkTo(comicImage.end, 4.dp)
            })
            ReadStatus(comicItem, modifier = Modifier.constrainAs(readStatus){
                top.linkTo(readProgress.bottom, 4.dp)
                end.linkTo(parent.end, 4.dp)
            })

        }
    }

    if (openDialog) { refresh() }
}



@Preview("Bookmark Button")
@Composable
fun BookmarkButtonPreview() {
    MangaMoreTheme {
        Surface {
            BookmarkButton(isBookmarked = false, onClick = { })
        }
    }
}

@Preview("Bookmark Button Bookmarked")
@Composable
fun BookmarkButtonBookmarkedPreview() {
    MangaMoreTheme {
        Surface {
            BookmarkButton(isBookmarked = true, onClick = { })
        }
    }
}

@Preview("Simple post card")
@Preview("Simple post card (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SimpleComicItemCardPreview() {
/*    AppTheme {
        Surface {
            ComicItemCard(ComicItem(
                1,
                2,
                "32424243",
            Uri.parse("we2321313"),
            Uri.parse("23131312"),
            ComicType.ZIP,
            3,
            4.6,
            5,
            Date(2222222222222222),
            true), {}, {}, {})
        }
    }*/
}
