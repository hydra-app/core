package knf.hydra.core.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Represents a weekly calendar
 *
 * @property days A map using [Calendar.DAY_OF_WEEK] as keys
 */
open class CalendarList(val days: Map<Int, Flow<PagingData<DirectoryModel>>>)