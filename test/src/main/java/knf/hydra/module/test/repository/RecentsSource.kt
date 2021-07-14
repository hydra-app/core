package knf.hydra.module.test.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.RecentModel
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecentsSource(private val bypassModel: BypassModel, private val bridge: MainDbBridge) : PagingSource<Int, RecentModel>() {
    override fun getRefreshKey(state: PagingState<Int, RecentModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecentModel> {
        return try {
            LoadResult.Page(
                withContext(Dispatchers.IO) { NetworkRepository.getRecents(bypassModel, bridge) },
                null,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}