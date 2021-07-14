package knf.hydra.core.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class CalendarList(val days: Map<Int, Flow<PagingData<DirectoryModel>>>)