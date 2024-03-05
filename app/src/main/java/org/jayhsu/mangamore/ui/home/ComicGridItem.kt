package org.jayhsu.mangamore.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
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
fun ComicGridItem(comicItem: ComicItem, appNavigation: AppNavigation) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(comicItem.thumbnailUri)
            .size(55) // Set the target size to load the image at.
            .build(),
        contentScale = ContentScale.Crop
    )
    Card(
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = {
                appNavigation.navigationToComicViewer(comicItem.comicId)
            })
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(55.dp),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Crop
            )
            Text(
                text = comicItem.name,
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}