package knf.hydra.module.test.models

import knf.hydra.core.models.ChapterModel

class TestChapterModel(seriesId: String, animeLink: String, chapLink: String, chapterNumber: Double, thumbLink: String, comments: String?): ChapterModel() {
    override var chapterLink: String = chapLink
    override var id: Int = "$seriesId-$chapterNumber".hashCode()
    override var type: String = "Chapter %s"
    override var number: Double = chapterNumber
    override var seriesLink: String? = animeLink
    override var thumbnailLink: String? = thumbLink
    override var commentsLink: String? = comments
}