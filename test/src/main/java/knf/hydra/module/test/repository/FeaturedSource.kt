package knf.hydra.module.test.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.data.FilterData
import knf.hydra.core.models.data.FilterItem
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.core.models.data.FilterResult
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeaturedSource(private val bypassModel: BypassModel) : PagingSource<Int, DirectoryModel>() {
    override fun getRefreshKey(state: PagingState<Int, DirectoryModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirectoryModel> {
        return try {
            val list = withContext(Dispatchers.IO) {
                NetworkRepository.getDirectoryPage(
                    1, bypassModel, FilterRequest(
                        listOf(
                            FilterResult(
                                FilterData("status[]", "", FilterData.Type.SINGLE, listOf()),
                                listOf(FilterItem("1", ""))
                            ),
                            FilterResult(
                                FilterData("order", "", FilterData.Type.SINGLE, listOf()),
                                listOf(FilterItem("rating", ""))
                            )
                        )
                    )
                ).take(5)
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