package knf.hydra.module.test.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.models.ContentItemModel
import knf.hydra.module.test.models.TestAnimeInfo
import knf.hydra.module.test.models.TestContentItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.text.DecimalFormat

class ChaptersSource (private val disqusVersion: String?, private  val constructor: TestAnimeInfo.ChapterConstructor) : PagingSource<Int, ContentItemModel>() {
    override fun getRefreshKey(state: PagingState<Int, ContentItemModel>): Int? {
        return state.anchorPosition
    }

    private fun generateCommentsLink(link:String): String?{
        disqusVersion?: return null
        return "https://disqus.com/embed/comments/?base=default&f=https-animeflv-net&t_u=${URLEncoder.encode(link, "utf-8")}&s_o=default#version=$disqusVersion"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentItemModel> {
        return try {
            params.key
            val list = withContext(Dispatchers.IO){
                constructor.chapterList.subList((params.key?:0)*10,constructor.chapterList.size).take(10)
                    .map {
                        val numFormatted = DecimalFormat("0.#").format(it.toDouble())
                        val chapLink = constructor.chapterLinkBase + numFormatted
                        TestContentItemModel(
                            constructor.seriesId,
                            constructor.seriesLink,
                            chapLink,
                            it.toDouble(),
                            String.format(constructor.thumbLinkBase,numFormatted),
                            generateCommentsLink(chapLink)
                        )
                    }
            }
            LoadResult.Page(
                list,
                null,
                if (list.size < 10) null else (params.key?:0) + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}