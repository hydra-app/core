/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

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