package knf.hydra.module.test.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.LayoutType
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
    override var infoLink: String = ""
    @Selector("img", attr = "abs:src")
    override var imageLink: String? = ""
    @Selector("span.Type")
    override var type: String? = ""
    override var category: Category = Category.ANIME
    @Selector("span.Type", converter = LayoutConverter::class)
    override var infoLayoutType: LayoutType = LayoutType.UNKNOWN
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

    class LayoutConverter: ElementConverter<LayoutType> {
        override fun convert(node: Element, selector: Selector): LayoutType {
            return if (node.className().contains("movie")) LayoutType.SINGLE else LayoutType.MULTIPLE
        }
    }
}

class DirectoryPage{
    @Selector("ul.ListAnimes li")
    var list = emptyList<TestDirectoryModel>()
}