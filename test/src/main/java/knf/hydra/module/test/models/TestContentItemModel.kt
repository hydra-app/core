package knf.hydra.module.test.models

import knf.hydra.core.models.ContentItemModel

class TestContentItemModel(seriesId: String, animeLink: String, chapLink: String, chapterNumber: Double, thumbLink: String, comments: String?): ContentItemModel() {
    override var itemLink: String = chapLink
    override var id: Int = "$seriesId-$chapterNumber".hashCode()
    override var type: String = "Chapter %s"
    override var number: Double = chapterNumber
    override var itemTitle: String? = null
    override var thumbnailLink: String? = thumbLink
    override var commentsLink: String? = comments
}