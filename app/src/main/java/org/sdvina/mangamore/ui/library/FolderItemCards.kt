package org.sdvina.mangamore.ui.library

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.sdvina.mangamore.R
import org.sdvina.mangamore.data.model.FolderItem
import org.sdvina.mangamore.data.enums.FolderType
import org.sdvina.mangamore.ui.AppNavigation
import org.sdvina.mangamore.ui.theme.MangaMoreTheme

@Composable
fun FolderItemName(folderItem: FolderItem, modifier: Modifier) {
 Text(text = folderItem.name,
            modifier = modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(24.dp),
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
}

@Composable
fun ComicCount(folderItem: FolderItem, modifier: Modifier) {
    Text(text = String.format(stringResource(R.string.comic_count), folderItem.comicCount),
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .padding(4.dp),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun FolderItemType(folderItem: FolderItem, modifier: Modifier) {
    val imageVector = when(folderItem.type){
        FolderType.LOCAL -> Icons.Filled.Folder
        FolderType.CLOUD -> Icons.Filled.Cloud
        else -> Icons.Filled.QuestionMark
    }
    Icon(
        imageVector = imageVector,
        modifier = modifier.padding(4.dp),
        contentDescription = null
    )
}

@Composable
fun FolderItemImage(
    folderItem: FolderItem,
    modifier: Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(folderItem.thumbnailUri)
            .size(600)
            .build(),
        contentDescription = null,
        alignment = Alignment.TopCenter,
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}

@Composable
fun FolderItemCard(
    appNavigation: AppNavigation,
    folderItem: FolderItem,
    onFolderItemSelected: (folderItem: FolderItem) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(bottom = 16.dp)
            .clickable(onClick = {
                onFolderItemSelected(folderItem)
                appNavigation.navigationToComicList()
            })
    ) {
        ConstraintLayout( modifier = Modifier.fillMaxWidth()){
            val ( folderImage, folderType, folderName, iconMore, comicCount) = createRefs()
            FolderItemImage(folderItem, Modifier.constrainAs(folderImage) {
                width = Dimension.matchParent
                linkTo(
                    start = parent.start,
                    end = parent.end
                )
                top.linkTo(parent.top)
            })
            FolderItemType(folderItem, Modifier.constrainAs(folderType){
                start.linkTo(parent.start, 4.dp)
                top.linkTo(folderImage.bottom, 4.dp)
                bottom.linkTo(parent.bottom, 4.dp)
            })
            FolderItemName(folderItem, Modifier.constrainAs(folderName){
                linkTo(
                    start = folderType.end,
                    end = iconMore.start
                )
                top.linkTo(folderImage.bottom)
            })
            CompositionLocalProvider() { // TODO LocalContent provides ContentAlpha.medium
                IconButton(
                    onClick = { openDialog = true },
                    modifier = Modifier.constrainAs(iconMore){
                        end.linkTo(parent.end, 4.dp)
                        top.linkTo(folderImage.bottom, 4.dp)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.cd_morevert)
                    )
                }
            }
            ComicCount(folderItem, Modifier.constrainAs(comicCount){
                top.linkTo(iconMore.bottom)
                end.linkTo(parent.end, 16.dp)
                bottom.linkTo(parent.bottom, 4.dp)
            } )
        }

    }

    if (openDialog) {
        // TODO 菜单
    }
}

@Preview("Simple post card")
@Preview("Simple post card (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SimpleFolderItemCardPreview() {
    MangaMoreTheme {
        Surface {
           /* FolderItemCard2(
                FolderItem(
                folderId = 1,
                name= "www",
                comicCount = 4,
                uri= "wqeqeqeqe",
                thumbnailUri = "234444444444",
                type = FolderType.LOCAL
                )
            )*/
        }
    }
}