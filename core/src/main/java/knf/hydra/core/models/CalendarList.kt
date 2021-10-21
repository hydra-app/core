package knf.hydra.core.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

open class CalendarList(val days: Map<Int, Flow<PagingData<DirectoryModel>>>)