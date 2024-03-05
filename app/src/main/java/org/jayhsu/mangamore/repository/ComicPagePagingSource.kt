package org.jayhsu.mangamore.repository

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.jayhsu.mangamore.data.model.ComicPageItem
import java.io.IOException

class ComicPagePagingSource(val context: Context, private val comicId: Long, private val fileNodeKey: String) : PagingSource<Int, ComicPageItem>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ComicPageItem> {
        return try {
            val nextPageNumber = params.key ?: 1
            val pageSize = params.loadSize
            val comicPageItems = searchComicPageItem(nextPageNumber, pageSize)
            LoadResult.Page(
                data = comicPageItems,
                prevKey = if (nextPageNumber > 1) nextPageNumber - 1 else null,
                nextKey = if (comicPageItems.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch(e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ComicPageItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    private fun searchComicPageItem(nextPageNumber: Int, pageSize: Int): List<ComicPageItem>{
        val comicPageItems: MutableList<ComicPageItem> = mutableListOf()
/*        val fromIndex = (nextPageNumber - 1) * pageSize
        val toIndex = nextPageNumber * pageSize
        val comicItemDetail = AppPreferences.getComicItemDetail(comicId)
        val childList = comicItemDetail!!.fileNodes.get(fileNodeKey)?.childList
        if(childList!= null){
          val subChildList = when( toIndex < childList.size  ){
                true -> childList.subList(fromIndex, toIndex)
                false ->childList.subList(fromIndex, childList.size -1)
            }
            for(subChildNode in subChildList) {
                if(subChildNode.fileUri == null) {
                    when(subChildNode.isDirectory){
                        true -> subChildNode.fileUri = null
                        false -> subChildNode.fileUri = DiskCache.getImageUri(context, comicItemDetail.comicItemUri,subChildNode.entryName, subChildNode.fileName)
                    }
                }
                if(!subChildNode.isDirectory){
                    comicPageItems.add(ComicPageItem(
                        uri = subChildNode.fileUri!!,
                        comicUri = comicItemDetail.comicItemUri,
                        pageName = subChildNode.originName,
                        byteArray = null,
                        pageNo = 1
                    ))

                }

            }
        }*/
        return comicPageItems
    }
}