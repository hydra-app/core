package knf.hydra.module.test.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.ChapterModel
import knf.hydra.module.test.models.TestAnimeInfo
import knf.hydra.module.test.models.TestChapterModel
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DecimalFormat

class ChaptersSource (private  val constructor: TestAnimeInfo.ChapterConstructor, private val bridge: MainDbBridge) : PagingSource<Int, ChapterModel>() {
    override fun getRefreshKey(state: PagingState<Int, ChapterModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChapterModel> {
        return try {
            val list = withContext(Dispatchers.IO){
                constructor.chapterList.subList((params.key?:0)*10,constructor.chapterList.size).take(10)
                    .map {
                        TestChapterModel(
                            constructor.seriesId,
                            constructor.seriesLink,
                            constructor.chapterLinkBase + DecimalFormat("0.#").format(it.toDouble()),
                            it.toDouble(),
                            String.format(constructor.thumbLinkBase,DecimalFormat("0.#").format(it.toDouble()))
                        )
                    }
            }
            withContext(Dispatchers.IO){
                list.forEach {
                    it.isChapterSeen = bridge.isChapterSeen(it.id, "knf.hydra.module.test")
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