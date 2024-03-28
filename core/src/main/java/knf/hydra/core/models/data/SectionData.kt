/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models.data

import knf.hydra.core.models.DirectoryModel

/**
 * Represents a custom section in home
 *
 * @property title Section title
 * @property data A [PagerData] containing a [PagingData](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingsource)
 * for the custom section.
 */
data class SectionData(val title: String, val data: PagerData<*, DirectoryModel>)