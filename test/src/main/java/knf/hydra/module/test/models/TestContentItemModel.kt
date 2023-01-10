/*
 * Created by @UnbarredStream on 10/01/23 14:15
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 12/12/22 14:55
 */

package knf.hydra.module.test.models

import knf.hydra.core.models.ContentItemModel
import knf.hydra.core.models.data.LinkData

class TestContentItemModel(seriesId: String, animeLink: String, chapLink: String, chapterNumber: Double, thumbLink: String, comments: String?, typeStr: String = "Chapter %s"): ContentItemModel() {
    override var itemLink: LinkData = LinkData(chapLink)
    override var id: Int = "$seriesId-$chapterNumber".hashCode()
    override var type: String = typeStr
    override var number: Double = chapterNumber
    override var itemTitle: String? = null
    override var thumbnailLink: String? = thumbLink
    override var commentsLink: String? = comments
    override var isMedia: Boolean = true
}