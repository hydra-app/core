package knf.hydra.module.test.extras

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import knf.hydra.core.HeadRepository
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.*
import knf.hydra.core.models.data.*
import knf.hydra.module.test.BuildConfig
import knf.hydra.module.test.db.DB
import knf.hydra.module.test.models.TestDirectoryModel
import knf.hydra.module.test.repository.*
import knf.hydra.module.test.retrofit.NetworkRepository
import knf.tools.bypass.containsAny
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

class Repository : HeadRepository() {

    override suspend fun recentsPager(
        bypassModel: BypassModel,
        bridge: MainDbBridge
    ): Flow<PagingData<RecentModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { RecentsSource(bypassModel, bridge) }
        ).flow
    }

    override fun infoPage(
        link: String,
        bypassModel: BypassModel,
        bridge: MainDbBridge
    ): Flow<InfoModel?> {
        return flow {
            emit(withContext(Dispatchers.IO) {
                NetworkRepository.getInfo(link, bypassModel, bridge)
            })
        }
    }

    override suspend fun lastRecents(
        bypassModel: BypassModel,
        bridge: MainDbBridge
    ): List<RecentModel> {
        val lastModel = bridge.getLastRecent(BuildConfig.APPLICATION_ID)
        val recentList = NetworkRepository.getRecents(bypassModel, bridge)
        if (recentList.isEmpty()){
            return emptyList()
        }
        bridge.saveLastRecent(RecentModel.Notify.fromRecent(BuildConfig.APPLICATION_ID, recentList.first()))
        return if (lastModel == null) {
            recentList
        }else {
            val foundIndex = recentList.indexOfFirst { it.id == lastModel.model.id }
            when{
                foundIndex == -1 -> recentList
                foundIndex != 0 -> recentList.subList(0, foundIndex - 1)
                else -> emptyList()
            }
        }
    }

    override suspend fun sourceDataType(link: String, bypassModel: BypassModel): SourceData.Type = SourceData.Type.VIDEO

    override fun sourceData(link: String, bypassModel: BypassModel): Flow<SourceData?> {
        return flow {
            val doc = Jsoup.connect(link).headers(bypassModel.asMap()).get()
            val scripts = doc.select("script[type]:not([src])")
            val data = scripts.let { elements ->
                elements.forEach { element ->
                    element.dataNodes().forEach {
                        val subData = it.wholeData
                        if (subData.contains("var videos =")) {
                            return@let subData
                        }
                    }
                }
                return@let ""
            }
            val jsonString = "videos = (\\{.*\\});".toRegex().find(data)?.destructured?.component1()
            if (jsonString != null) {
                val items = mutableListOf<SourceItem>()
                val json = JSONObject(jsonString)
                val downloads = doc.select(".RTbl.Dwnl")
                listOf("SUB","LAT").forEach { lang ->
                    if (json.has(lang)){
                        val array = json.getJSONArray(lang)
                        for (sub in 0 until array.length()){
                            val subItem = array.getJSONObject(sub)
                            val sLink = subItem.getString("code")
                            val canLinkDownload = !sLink.contains("mega.nz") && !sLink.contains("hqq.tv")
                            val videoQuality = when {
                                sLink.containsAny("embedsito.com", "ok.ru") -> SourceItem.Quality.MULTIPLE
                                else -> SourceItem.Quality.MEDIUM
                            }
                            items.add(SourceItem(subItem.getString("title"),sLink, type = lang, quality = videoQuality, canDownload = canLinkDownload))
                        }
                    }
                    downloads.select("tr:contains($lang)").forEach { element ->
                        val name = element.select("td").first().text()
                        val dLink = element.select("a").attr("href")
                        val videoQuality = when {
                            dLink.containsAny("embedsito.com", "ok.ru") -> SourceItem.Quality.MULTIPLE
                            else -> SourceItem.Quality.MEDIUM
                        }
                        when{
                            dLink.contains("mega.nz") -> {
                                if (items.find { it.link.substringAfterLast("#") == dLink.substringAfterLast("#") } == null)
                                    items.add(SourceItem(name, dLink, type = lang, quality = videoQuality, canDownload = false))
                            }
                            else -> {
                                if (items.find { it.link.substringAfterLast("/") == dLink.substringAfterLast("/") } == null)
                                    items.add(SourceItem(name, dLink, type = lang, quality = videoQuality))
                            }
                        }
                    }
                }
                emit(VideoSource(items))
            } else
                emit(null)
        }
    }

    override suspend fun directoryFilters(bypassModel: BypassModel): List<FilterData> {
        return withContext(Dispatchers.IO) {
            val filterList = mutableListOf<FilterData>()
            val document = Jsoup.connect("https://animeflv.net/browse")
                .followRedirects(true)
                .headers(bypassModel.asMap())
                .header("device", "computer")
                .get()
            document.select("div.filters select").forEach { element ->
                val select = element.select("select")
                val key = element.attr("name")
                val type =
                    if (element.hasAttr("multiple")) FilterData.Type.MULTIPLE else FilterData.Type.SINGLE
                val filterItems =
                    select.select("option").map { FilterItem(it.attr("value"), it.text()) }
                val name = when (key) {
                    "genre[]" -> "Genero"
                    "year[]" -> "Año"
                    "type[]" -> "Tipo"
                    "status[]" -> "Estado"
                    "order" -> "Orden"
                    else -> "Otro"
                }
                filterList.add(FilterData(key, name, type, filterItems))
            }
            filterList
        }
    }

    override suspend fun directoryPager(
        bypassModel: BypassModel,
        filters: FilterRequest?
    ): Flow<PagingData<DirectoryModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { DirectorySource(bypassModel, filters) }
        ).flow
    }

    override suspend fun searchPager(
        query: String?,
        bypassModel: BypassModel,
        filters: FilterRequest?
    ): Flow<PagingData<SearchModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchSource(query, bypassModel, filters) }
        ).flow
    }

    override suspend fun calendarList(bypassModel: BypassModel, day: Int): Flow<CalendarList> = flow {
        if (DB.isActive() && DB.INSTANCE.calendarDao().countAll() > 0){
            val cachedMap = if (day != -1){
                mapOf(day to DB.INSTANCE.calendarDao().getByDay(day))
            }else {
                mapOf<Int, List<DirectoryModel>>(
                    Calendar.SUNDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.SUNDAY),
                    Calendar.MONDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.MONDAY),
                    Calendar.TUESDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.TUESDAY),
                    Calendar.WEDNESDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.WEDNESDAY),
                    Calendar.THURSDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.THURSDAY),
                    Calendar.FRIDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.FRIDAY),
                    Calendar.SATURDAY to DB.INSTANCE.calendarDao().getByDay(Calendar.SATURDAY)
                )
            }
            emit(
                CalendarList(cachedMap.mapValues {
                    Pager(
                        config = PagingConfig(24),
                        pagingSourceFactory = { CalendarSource(it.value) }
                    ).flow
                })
            )
        }
        val daysMap = mapOf<Int,MutableList<TestDirectoryModel>>(
            Calendar.SUNDAY to mutableListOf(),
            Calendar.MONDAY to mutableListOf(),
            Calendar.TUESDAY to mutableListOf(),
            Calendar.WEDNESDAY to mutableListOf(),
            Calendar.THURSDAY to mutableListOf(),
            Calendar.FRIDAY to mutableListOf(),
            Calendar.SATURDAY to mutableListOf()
        )
        var page = 1
        var hasMore = true
        while (hasMore){
            try {
                val list = NetworkRepository.getCalendarPage(page, bypassModel)
                list.forEach { item ->
                    try {
                        val doc = Jsoup.connect(item.seriesLink).headers(bypassModel.asMap()).get()
                        val html = doc.html()
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
                            daysMap[calendar.get(Calendar.DAY_OF_WEEK)]?.add(item)
                        }
                    }catch (e:Exception){
                        //e.printStackTrace()
                    }
                }
                hasMore = list.size == 24
            }catch (e:Exception){
                //
            }
            page++
        }
        if (DB.isActive()){
            DB.INSTANCE.calendarDao().nuke()
            daysMap.forEach { entry ->
                entry.value.onEach { it.releaseDay = entry.key }
                DB.INSTANCE.calendarDao().insertAll(entry.value)
            }
        }
        emit(
            CalendarList(daysMap.mapValues {
                Pager(
                    config = PagingConfig(24),
                    pagingSourceFactory = { CalendarSource(it.value) }
                ).flow
            })
        )
    }

    private fun createSection(bypassModel: BypassModel, title :String, query: String, limit: Int): SectionData {
        return SectionData(title,Pager(config = PagingConfig(limit), pagingSourceFactory = { BestSource(bypassModel,query,limit) }).flow)
    }

    override suspend fun customHomeSections(bypassModel: BypassModel): List<SectionData> {
        return listOf(
            createSection(bypassModel, "Featured", "status[]=1&order=rating", 5),
            run {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                createSection(bypassModel, "Best $year","year[]=$year&order=rating",15)
            },
            createSection(bypassModel,"Best overall", "order=rating", 15)
        )
    }
}