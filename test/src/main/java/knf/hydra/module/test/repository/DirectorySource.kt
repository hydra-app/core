package knf.hydra.module.test.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DirectorySource(private val bypassModel: BypassModel, private val filters: FilterRequest?) : PagingSource<Int, DirectoryModel>() {
    override fun getRefreshKey(state: PagingState<Int, DirectoryModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirectoryModel> {
        return try {
            val list = withContext(Dispatchers.IO) { NetworkRepository.getDirectoryPage(params.key?:1,bypassModel, filters) }
            LoadResult.Page(
                list,
                null,
                if (list.size < 24) null else (params.key?:1) + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}