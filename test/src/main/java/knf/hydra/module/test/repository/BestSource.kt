package knf.hydra.module.test.repository

import androidx.annotation.IntRange
import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.FilterData
import knf.hydra.core.models.data.FilterItem
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.core.models.data.FilterResult
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BestSource(private val bypassModel: BypassModel, private val query: String, @IntRange(from = 1, to = 24) private val limit: Int) : PagingSource<Int, DirectoryModel>() {
    override fun getRefreshKey(state: PagingState<Int, DirectoryModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirectoryModel> {
        return try {
            val list = withContext(Dispatchers.IO) {
                NetworkRepository.getDirectoryPage(
                    1, bypassModel, FilterRequest(
                        query.split("&").map {
                            val split = it.split("=")
                            FilterResult(
                                FilterData(split[0], "", FilterData.Type.SINGLE, listOf()),
                                listOf(FilterItem(split[1], ""))
                            )
                        }
                    )
                ).take(limit)
            }
            LoadResult.Page(
                list,
                null,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}