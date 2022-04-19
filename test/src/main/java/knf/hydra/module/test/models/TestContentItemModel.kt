/*
 * Created by @UnbarredStream on 18/04/22 19:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 18/04/22 17:06
 */

package knf.hydra.module.test.models

import knf.hydra.core.models.ContentItemModel
import knf.hydra.core.models.data.LinkData

class TestContentItemModel(seriesId: String, animeLink: String, chapLink: String, chapterNumber: Double, thumbLink: String, comments: String?): ContentItemModel() {
    override var itemLink: LinkData = LinkData(chapLink)
    override var id: Int = "$seriesId-$chapterNumber".hashCode()
    override var type: String = "Chapter %s"
    override var number: Double = chapterNumber
    override var itemTitle: String? = null
    override var thumbnailLink: String? = thumbLink
    override var commentsLink: String? = comments
    override var isMedia: Boolean = true
}