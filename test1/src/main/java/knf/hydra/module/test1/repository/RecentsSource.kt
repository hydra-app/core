package knf.hydra.module.test1.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.models.RecentModel
import knf.hydra.module.test1.models.TestRecentModel

class RecentsSource: PagingSource<Int,RecentModel>() {
    override fun getRefreshKey(state: PagingState<Int, RecentModel>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecentModel> {
        val position = params.key ?: 1
        val prev = if (position == 1) null else position - 1
        val next = position + 1
        return LoadResult.Page((0..19).map { TestRecentModel() },prev,next)
    }
}