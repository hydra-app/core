package knf.hydra.core.models.data

import androidx.paging.PagingData
import knf.hydra.core.models.DirectoryModel
import kotlinx.coroutines.flow.Flow

data class SectionData(val title: String, val data: Flow<PagingData<DirectoryModel>>)