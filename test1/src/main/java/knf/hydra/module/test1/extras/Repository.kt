package knf.hydra.module.test1.extras

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.SourceData
import knf.hydra.module.test1.repository.RecentsSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository : HeadRepository(){

    override fun infoPage(link: String, bypassModel: BypassModel): Flow<InfoModel?> {
        return flow {
            emit(null)
        }
    }

    override fun sourceData(link: String, bypassModel: BypassModel): Flow<SourceData?> {
        return flow { emit(null) }
    }

    override suspend fun recentsPager(bypassModel: BypassModel): Flow<PagingData<RecentModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecentsSource() }
        ).flow
    }
}