package knf.hydra.module.test.models

import android.util.Log
import androidx.annotation.Keep
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.*
import knf.hydra.core.models.ChapterModel
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.ExtraData
import knf.hydra.core.models.data.RankingData
import knf.hydra.module.test.repository.ChaptersSource
import knf.hydra.module.test.repository.RecentsSource
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.droidsonroids.jspoon.ElementConverter
import pl.droidsonroids.jspoon.annotation.Selector
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

@Entity
@TypeConverters(InfoModel.Converters::class)
class TestAnimeInfo : InfoModel() {
    @PrimaryKey
    @Selector(".Strs.RateIt", attr = "data-id")
    override var id: Int = Random.nextInt()

    @Selector("h1.Title")
    override var name: String = "???"

    @Selector("link[rel=canonical]", attr = "href")
    override var link: String = ""

    override var category: Category = Category.ANIME

    @Selector("div.Image img", attr = "abs:src")
    override var coverImage: String? = null

    @Selector("div.Description")
    override var description: String? = null

    @Selector("nav.Nvgnrs", converter = GenresConverter::class)
    override var genres: List<String>? = null

    @Embedded(prefix = "ranking_")
    @Selector("div.Votes", converter = RankingConverter::class)
    override var ranking: RankingData? = null

    @Ignore
    @Selector(":root", converter = RelatedConverter::class)
    override var related: List<Related>? = null

    @Ignore
    @Selector(":root", converter = MusicConverter::class)
    override var music: Flow<List<Music>?>? = null

    @Embedded(prefix = "state_")
    @Selector(":root", converter = StateConverter::class)
    override var state: StateData? = null

    @Selector("span.Type")
    override var type: String? = null

    @Ignore
    @Selector(":root", converter = ChaptersConverter::class)
    override var chaptersPaging: Flow<PagingData<ChapterModel>>? = null

    @Embedded(prefix = "data_")
    @Selector(":root", converter = ExtraDataConverter::class)
    override var extraData: List<ExtraData> = emptyList()

    @Keep
    class GenresConverter @Keep constructor() : ElementConverter<List<String>> {
        override fun convert(node: Element, selector: Selector): List<String> {
            return node.select("a").map { it.text() }
        }
    }

    @Keep
    class RankingConverter @Keep constructor() : ElementConverter<RankingData?> {
        override fun convert(node: Element, selector: Selector): RankingData? {
            return try {
                val stars = node.select("span#votes_prmd").text().toDouble()
                val votes = node.select("span#votes_nmbr").text().toInt()
                RankingData(stars, votes)
            } catch (e: Exception) {
                null
            }
        }
    }

    @Keep
    class RelatedConverter @Keep constructor() : ElementConverter<List<Related>?> {
        override fun convert(node: Element, selector: Selector): List<Related>? {
            val relatedList = node.select("ul.ListAnmRel")
            if (relatedList.isEmpty()) {
                return null
            }
            val rels = relatedList.select("li")
            val links = rels.map { it.select("a").attr("abs:href") }
            val objs =
                links.map { NetworkRepository.getRelatedInfo(it, NetworkRepository.currentBypass) }
            rels.map { it.ownText().trim().removeSurrounding("(", ")") }
                .forEachIndexed { index, relation ->
                    objs[index]?.let {
                        it.link = links[index]
                        it.relation = relation
                    }
                }
            return objs.filterNotNull()
        }
    }

    @Keep
    class StateConverter @Keep constructor() : ElementConverter<StateData> {
        override fun convert(node: Element, selector: Selector): StateData {
            val animeState = node.select("p.AnmStts").first()
            val status =
                if (animeState.hasClass("A")) StateData.Type.COMPLETED else StateData.Type.EMISSION
            var emissionDay: StateData.EmissionDay? = null
            if (status == StateData.Type.EMISSION) {
                val html = node.html()
                val info =
                    "anime_info = \\[(.*)\\];".toRegex().find(html)?.destructured?.component1()
                        ?.split(",")?.map { it.replace("\"", "") }
                if (info?.size == 4) {
                    val calendar = Calendar.getInstance().apply {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(info.last())
                            ?.let {
                                time = it
                            }
                    }
                    emissionDay =
                        when (calendar.get(Calendar.DAY_OF_WEEK)) {
                            2 -> StateData.EmissionDay.MONDAY
                            3 -> StateData.EmissionDay.TUESDAY
                            4 -> StateData.EmissionDay.WEDNESDAY
                            5 -> StateData.EmissionDay.THURSDAY
                            6 -> StateData.EmissionDay.FRIDAY
                            7 -> StateData.EmissionDay.SATURDAY
                            1 -> StateData.EmissionDay.SUNDAY
                            else -> null
                        }
                }
            }
            return StateData(status, emissionDay)
        }
    }

    @Keep
    class ExtraDataConverter @Keep constructor() : ElementConverter<List<ExtraData>> {
        override fun convert(node: Element, selector: Selector): List<ExtraData> {
            val names = node.select(".TxtAlt").joinToString("\n") { it.text().trim() }
            return if (names.isNotBlank())
                listOf(ExtraData("Nombres alternativos", names))
            else
                emptyList()
        }
    }

    @Keep
    class MusicConverter @Keep constructor() : ElementConverter<Flow<List<Music>?>?> {
        override fun convert(node: Element, selector: Selector): Flow<List<Music>?> {
            return flow {
                try {
                    /*val title = node.select("h1.Title").text()
                    val search = withContext(Dispatchers.IO) {
                        Jsoup.connect("https://www.reddit.com/r/AnimeThemes/wiki/anime_index").get()
                    }.select("a:containsOwn($title)")
                    if (search.isEmpty()) {
                        emit(null)
                        return@flow
                    }
                    val selected = if (search.size == 1) {
                        search.first()
                    } else {
                        search.minByOrNull {
                            abs(it.text().substringBefore("(").trim().length - title.length)
                        }!!
                    }
                    val link = selected.attr("abs:href")
                    Log.e("Music","Found: $link")
                    val id = link.substringAfterLast("#")
                    Log.e("Music","Search id: $id")
                    val animeSearch = withContext(Dispatchers.IO) { Jsoup.connect(link).get() }
                    val animeHeader = animeSearch.select("h3#$id").first()
                    if (animeHeader == null) {
                        emit(null)
                        return@flow
                    }
                    var songsTable = animeHeader.nextElementSibling()
                    if (songsTable.tagName() == "p")
                        songsTable = songsTable.nextElementSibling()
                    val songs = songsTable.select("tbody tr")
                    val songList = mutableListOf<Music>()
                    songs.forEach {
                        val regex = "(.*) \"(.*)\"".toRegex().find(it.select("td").first().text())
                        if (regex != null) {
                            val (sSub, sTitle) = regex.destructured
                            val sLink = it.select("td a").first().attr("href")
                            val response =
                                Jsoup.connect(sLink).ignoreContentType(true).followRedirects(false)
                                    .execute()
                            if (response.statusCode() != 200)
                                songList.add(Music(sTitle, sLink, sSub))
                        }
                    }
                    val result = if (songList.isNotEmpty())
                        songList
                    else
                        null
                    emit(result)*/
                    val title = node.select("h1.Title").text()
                    val searchJson = withContext(Dispatchers.IO) {
                        URL("https://themes.moe/api/anime/search/$title").readText()
                    }
                    if (JSONArray(searchJson).length() == 0){
                        emit(null)
                        return@flow
                    }
                    val songsJson = withContext(Dispatchers.IO) {
                        Jsoup.connect("https://themes.moe/api/themes/search")
                            .ignoreContentType(true)
                            .method(Connection.Method.POST)
                            .requestBody(searchJson)
                            .execute().body()
                    }
                    val songsArray = JSONArray(songsJson)
                    if (songsArray.length() == 0){
                        emit(null)
                        return@flow
                    }
                    val result = songsArray.getJSONObject(0).getJSONArray("themes")
                    val songList = mutableListOf<Music>()
                    for (index in 0 until result.length()){
                        val json = result.getJSONObject(index)
                        songList.add(Music(
                            json.getString("themeName"),
                            json.getJSONObject("mirror").getString("mirrorURL"),
                            json.getString("themeType")
                        ))
                    }
                    if (songList.isNotEmpty())
                        emit(songList)
                    else
                        emit(null)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(null)
                }
            }
        }
    }

    @Keep
    class ChaptersConverter @Keep constructor() :
        ElementConverter<Flow<PagingData<ChapterModel>>?> {
        override fun convert(node: Element, selector: Selector): Flow<PagingData<ChapterModel>>? {
            val id = node.select(".Strs.RateIt").attr("data-id")
            val link = node.select("link[rel=canonical]").attr("href")
            val chapLinkBase = link.replace("/anime/", "/ver/") + "-"
            val thumbLinkBase = "https://cdn.animeflv.net/screenshots/$id/%s/th_3.jpg"
            val data = "episodes = \\[\\[(.*)\\]\\];".toRegex()
                .find(node.html())?.destructured?.component1()
            val chapList = data?.let { d ->
                d.split("],[").map { it.substringBeforeLast(",") }
            } ?: return null
            return Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    ChaptersSource(
                        ChapterConstructor(
                            id,
                            link,
                            chapLinkBase,
                            thumbLinkBase,
                            chapList
                        ), NetworkRepository.currentDbBridge
                    )
                }
            ).flow
        }
    }

    data class ChapterConstructor(
        val seriesId: String,
        val seriesLink: String,
        val chapterLinkBase: String,
        val thumbLinkBase: String,
        val chapterList: List<String>
    )
}