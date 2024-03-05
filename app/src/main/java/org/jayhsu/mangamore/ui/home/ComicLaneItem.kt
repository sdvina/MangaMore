package org.jayhsu.mangamore.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import org.jayhsu.mangamore.data.model.ComicItem
import org.jayhsu.mangamore.ui.AppNavigation

@Composable
fun ComicLaneItem(comicItem: ComicItem, appNavigation: AppNavigation) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(comicItem.thumbnailUri)
            .size(180, 160) // Set the target size to load the image at.
            .build(),
        contentScale = ContentScale.Crop
    )
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
            .clickable(onClick = {
                appNavigation.navigationToComicViewer(comicItem.comicId)
            })
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painter,
                modifier = Modifier
                    .width(180.dp)
                    .height(160.dp),
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop
            )
            Text(
                text = "${comicItem.name}: ${comicItem.name}",
                //style = typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}