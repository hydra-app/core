package knf.hydra.core.models.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

/**
 * Pager data representing a [PagingData]
 *
 * @param Key Used for paging
 * @param Value Result type
 * @property pageSize Page size for the [PagingData]
 * @property source [PagerData] source to be created
 */
data class PagerData<Key : Any, Value : Any>(
    val pageSize: Int,
    val source: PagingSource<Key, Value>
) {
    /** @suppress */
    private val config: PagingConfig = PagingConfig(pageSize = pageSize, enablePlaceholders = false)
    /** @suppress */
    fun createPager(): Flow<PagingData<Value>> {
        return Pager(
            config = config,
            pagingSourceFactory = { source }
        ).flow
    }

    /** @suppress */
    suspend fun createFullList(): List<Value> {
        val list = mutableListOf<Value>()
        val initial = source.load(
            PagingSource.LoadParams.Refresh(
                null,
                config.initialLoadSize,
                config.enablePlaceholders
            )
        )
        if (initial is PagingSource.LoadResult.Page) {
            list.addAll(initial.data)
            if (initial.nextKey != null) {
                var hasNext = true
                var lastResult: PagingSource.LoadResult.Page<Key, Value> = initial
                var retryCount = 0
                while (hasNext) {
                    val result = source.load(
                        PagingSource.LoadParams.Append(
                            lastResult.nextKey!!,
                            config.initialLoadSize,
                            config.enablePlaceholders
                        )
                    )
                    if (result is PagingSource.LoadResult.Page) {
                        lastResult = result
                        hasNext = result.nextKey != null
                        retryCount = 0
                        list.addAll(result.data)
                    } else if (retryCount > 3) {
                        hasNext = false
                    } else {
                        retryCount++
                    }
                }
            }
        }
        return list
    }
}