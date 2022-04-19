/*
 * Created by @UnbarredStream on 18/04/22 19:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 18/04/22 18:06
 */

package knf.hydra.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Minimized version of [ContentItemModel]
 *
 * @property id [ContentItemModel id][ContentItemModel.id]
 * @property name [ContentItemModel] name created with the [type][ContentItemModel.type]
 * @property number [ContentItemModel number][ContentItemModel.number]
 * @property link [ContentItemModel link][ContentItemModel.itemLink]
 * @property isMedia [ContentItemModel isMedia][ContentItemModel.isMedia]
 * @property thumbnailLink [ContentItemModel thumbnail][ContentItemModel.thumbnailLink]
 */
@Parcelize
data class ContentItemMin(
    var id: Int = 0,
    var name: String = "",
    var number: Double = 0.0,
    var link: String = "",
    var isMedia: Boolean = true,
    var thumbnailLink: String? = null
): Parcelable