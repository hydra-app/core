package knf.hydra.module.test1.models

import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.Category
import kotlin.random.Random


class TestRecentModel: RecentModel() {
    override var id: Int = Random.nextInt()
    override var seriesId: Int = Random.nextInt()
    override var episodeLink: String = "test link"
    override var seriesLink: String = "test link"
    override var name: String = "Test item 1"
    override var type: String = "Chapter %s"
    override var chapter: Double = 0.0
    override var category: Category = Category.UNKNOWN
}