/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @suppress */
@Parcelize
data class ContentItemMin(
    var id: Int = 0,
    var name: String = "",
    var number: Double = 0.0,
    var link: String = "",
    var thumbnailLink: String? = null
): Parcelable