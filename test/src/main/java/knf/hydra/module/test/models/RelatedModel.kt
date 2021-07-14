package knf.hydra.module.test.models

import androidx.room.Embedded
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.RankingData
import pl.droidsonroids.jspoon.annotation.Selector
import kotlin.random.Random

class RelatedModel: InfoModel.Related() {
    @Selector(".Strs.RateIt", attr = "data-id")
    override var id: Int = Random.nextInt()
    @Selector("h1.Title")
    override var name: String = "???"
    @Selector("div.Image img", attr = "abs:src")
    override var image: String? = null
    @Embedded(prefix = "related_ranking_")
    @Selector("div.Votes", converter = TestAnimeInfo.RankingConverter::class)
    override var ranking: RankingData? = null
}