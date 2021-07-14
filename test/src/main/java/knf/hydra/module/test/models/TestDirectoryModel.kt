package knf.hydra.module.test.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.data.RankingData
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector

@Entity(tableName = "calendar")
class TestDirectoryModel: DirectoryModel() {
    @PrimaryKey
    @Selector("img", attr = "src", regex = "/(\\d+)\\.")
    override var id: Int = 0
    @Selector("h3.Title")
    override var name: String = ""
    @Selector("a",attr = "abs:href")
    override var seriesLink: String = ""
    @Selector("img", attr = "abs:src")
    override var imageLink: String? = ""
    @Selector("span.Type")
    override var type: String? = ""
    @Embedded
    @Selector(":root", converter = RankingConverter::class)
    override var rankingData: RankingData? = null
    var releaseDay: Int? = null

    class RankingConverter: ElementConverter<RankingData>{
        override fun convert(node: Element, selector: Selector): RankingData {
            val stars = node.select("span.Vts.fa-star").text().toDouble()
            return RankingData(stars)
        }
    }
}

class DirectoryPage{
    @Selector("ul.ListAnimes li")
    var list = emptyList<TestDirectoryModel>()
}