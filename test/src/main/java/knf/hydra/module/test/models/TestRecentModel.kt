/*
 * Created by @UnbarredStream on 19/06/22 13:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 19/06/22 13:36
 */

package knf.hydra.module.test.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.Category
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import kotlin.random.Random

@Entity
class TestRecentModel : RecentModel() {
    @PrimaryKey
    @Selector(":root", converter = IdExtractor::class)
    override var id: Int = Random.nextInt()

    @Selector("img", converter = SeriesIdExtractor::class)
    override var infoId: Int = Random.nextInt()

    @Selector("img", converter = ImageExtractor::class)
    override var infoImage: String? = null

    @Selector(":root", converter = ChapterImageExtractor::class)
    override var contentThumbnail: String? = null

    @Selector("a", attr = "abs:href")
    override var link: String = ""

    @Selector("a", converter = SeriesLinkExtractor::class)
    override var infoLink: String = ""

    @Selector("strong.Title")
    override var name: String = ""

    @Selector("span.Capi", converter = ChapterExtractor::class)
    override var number: Double = 0.0

    override var category: Category = Category.ANIME

    override var type: String = "Chapter %s"

    override var isMedia: Boolean = true

    class IdExtractor : ElementConverter<Int> {
        override fun convert(node: Element, selector: Selector): Int {
            val seriesId = node.select("img").attr("src").substringAfterLast("/").substringBeforeLast(".")
            val chapter = node.select("span.Capi").text().substringAfterLast(" ").toDouble()
            return "$seriesId-$chapter".hashCode()
        }
    }

    class SeriesIdExtractor : ElementConverter<Int> {
        override fun convert(node: Element, selector: Selector): Int {
            return try{  node.attr("src").substringAfterLast("/").substringBeforeLast(".").toInt() } catch (e:Exception) { -1 }
        }
    }

    class ImageExtractor : ElementConverter<String> {
        override fun convert(node: Element, selector: Selector): String {
            return "https://animeflv.net/uploads/animes/covers/" + node.attr("src")
                .substringAfterLast("/")
        }
    }

    class ChapterImageExtractor : ElementConverter<String> {
        override fun convert(node: Element, selector: Selector): String {
            val id = node.select("img").first().attr("src").substringAfterLast("/").substringBeforeLast(".")
            val number = node.select("span.Capi").text().substringAfterLast(" ").trim().toInt()
            return "https://cdn.animeflv.net/screenshots/$id/$number/th_3.jpg"
        }
    }

    class SeriesLinkExtractor : ElementConverter<String> {
        override fun convert(node: Element, selector: Selector): String {
            return "https://animeflv.net/anime/" + node.attr("href").substringAfterLast("/")
                .substringBeforeLast("-")
        }
    }

    class ChapterExtractor : ElementConverter<Double> {
        override fun convert(node: Element, selector: Selector): Double {
            return node.text().substringAfterLast(" ").toDouble()
        }
    }
}

class RecentsPage {
    @Selector("ul.ListEpisodios li")
    var list = emptyList<TestRecentModel>()
}