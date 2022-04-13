/*
 * Created by @UnbarredStream on 13/04/22 11:59
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 13/04/22 11:43
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
 * @property thumbnailLink [ContentItemModel thumbnail][ContentItemModel.thumbnailLink]
 */
@Parcelize
data class ContentItemMin(
    var id: Int = 0,
    var name: String = "",
    var number: Double = 0.0,
    var link: String = "",
    var thumbnailLink: String? = null
): Parcelable