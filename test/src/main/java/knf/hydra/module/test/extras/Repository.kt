/*
 * Created by @UnbarredStream on 29/04/23 00:40
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 27/04/23 23:10
 */

package knf.hydra.module.test.extras

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.ContentItemMin
import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.CalendarDay
import knf.hydra.core.models.data.ExtraDirectoryRequest
import knf.hydra.core.models.data.FilterData
import knf.hydra.core.models.data.FilterItem
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.core.models.data.FilterResult
import knf.hydra.core.models.data.NotifyData
import knf.hydra.core.models.data.PagerData
import knf.hydra.core.models.data.ReviewResult
import knf.hydra.core.models.data.SectionData
import knf.hydra.core.models.data.SourceData
import knf.hydra.core.models.data.VideoItem
import knf.hydra.core.models.data.VideoSource
import knf.hydra.module.test.repository.BestSource
import knf.hydra.module.test.repository.DirectorySource
import knf.hydra.module.test.repository.RecentsSource
import knf.hydra.module.test.repository.SearchSource
import knf.hydra.module.test.retrofit.NetworkRepository
import knf.tools.bypass.containsAny
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Jsoup
import java.util.Calendar

class Repository : HeadRepository() {

    override suspend fun recentsPagerData(bypassModel: BypassModel): PagerData<*, RecentModel> {
        return PagerData(20, RecentsSource(bypassModel))
    }

    override fun infoPage(
        link: String,
        bypassModel: BypassModel
    ): Flow<InfoModel?> {
        return flow {
            emit(withContext(Dispatchers.IO) {
                NetworkRepository.getInfo(link, bypassModel)
            })
        }
    }

    override suspend fun lastRecents(data: NotifyData.Request): NotifyData.Response {
        val lastModel = data.lastRecent
        val recentList = NetworkRepository.getRecents(data.bypassModel)
        if (recentList.isEmpty()) {
            return NotifyData.Response(null, emptyList())
        }
        return NotifyData.Response(recentList.first(),
            if (lastModel == null) {
                recentList
            } else {
                mutableListOf<RecentModel>().apply {
                    recentList.forEach {
                        if (it.id == lastModel.model.id) return@apply
                        add(it)
                    }
                }
            }
        )
    }

    override suspend fun sourceData(content: ContentItemMin, bypassModel: BypassModel): SourceData<*> {
        return VideoSource(
            flow {
                val doc = Jsoup.connect(content.link).headers(bypassModel.asMap(NetworkRepository.defaultCookies)).get()
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
                    val items = mutableListOf<VideoItem>()
                    val json = JSONObject(jsonString)
                    val downloads = doc.select(".RTbl.Dwnl")
                    listOf("SUB", "LAT").forEach { lang ->
                        if (json.has(lang)) {
                            val array = json.getJSONArray(lang)
                            for (sub in 0 until array.length()) {
                                val subItem = array.getJSONObject(sub)
                                val sLink = subItem.getString("code")
                                val canLinkDownload = !sLink.containsAny("mega.nz", "hqq.tv")
                                val videoQuality = when {
                                    sLink.containsAny(
                                        "embedsito.com",
                                        "ok.ru"
                                    ) -> VideoItem.Quality.MULTIPLE
                                    else -> VideoItem.Quality.MEDIUM
                                }
                                items.add(
                                    VideoItem(
                                        subItem.getString("title"),
                                        sLink,
                                        type = lang,
                                        quality = videoQuality,
                                        canDownload = canLinkDownload
                                    )
                                )
                            }
                        }
                        downloads.select("tr:contains($lang)").forEach { element ->
                            val name = element.select("td").first()?.text()?: "Unk"
                            val dLink = element.select("a").attr("href")
                            val videoQuality = when {
                                dLink.containsAny(
                                    "embedsito.com",
                                    "ok.ru"
                                ) -> VideoItem.Quality.MULTIPLE
                                else -> VideoItem.Quality.MEDIUM
                            }
                            when {
                                dLink.contains("mega.nz") -> {
                                    if (items.find { it.link.substringAfterLast("#") == dLink.substringAfterLast("#") } == null)
                                        items.add(
                                            VideoItem(
                                                name,
                                                dLink,
                                                type = lang,
                                                quality = videoQuality,
                                                canDownload = false
                                            )
                                        )
                                }
                                else -> {
                                    if (items.find { it.link.substringAfterLast("/") == dLink.substringAfterLast("/") } == null)
                                        items.add(
                                            VideoItem(
                                                name,
                                                dLink,
                                                type = lang,
                                                quality = videoQuality
                                            )
                                        )
                                }
                            }
                        }
                    }
                    emit(items)
                } else
                    emit(emptyList())
            }
        )
    }

    override suspend fun directoryFilters(bypassModel: BypassModel): List<FilterData> {
        return withContext(Dispatchers.IO) {
            val filterList = mutableListOf<FilterData>()
            val document = Jsoup.connect("https://animeflv.net/browse")
                .followRedirects(true)
                .headers(bypassModel.asMap(NetworkRepository.defaultCookies))
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

    override suspend fun directoryPagerData(
        bypassModel: BypassModel,
        filters: FilterRequest?
    ): PagerData<*, DirectoryModel> {
        return PagerData(24, DirectorySource(bypassModel, filters))
    }

    override suspend fun searchPagerData(
        query: String?,
        bypassModel: BypassModel,
        filters: FilterRequest?
    ): PagerData<*, DirectoryModel> {
        return PagerData(24, SearchSource(query, bypassModel, filters))
    }

    override suspend fun calendarByDayData(
        bypassModel: BypassModel,
        day: CalendarDay
    ): PagerData<*, DirectoryModel> {
        val list = CalendarManager.getDay(bypassModel, day)
        return PagerData(50, object : PagingSource<Int, DirectoryModel>() {
            override fun getRefreshKey(state: PagingState<Int, DirectoryModel>): Int? = state.anchorPosition

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DirectoryModel> {
                return LoadResult.Page(list, null, null)
            }
        })
    }

    override suspend fun analyticsRecommended(
        bypassModel: BypassModel,
        events: List<Analytics.Event>
    ): Flow<List<DirectoryModel>> = flow {
        val filter = FilterResult(FilterData("genre","",FilterData.Type.SINGLE, emptyList()), events.take(3).map { FilterItem(it.payload.orEmpty(),"") })
        val order = FilterResult(FilterData("order","",FilterData.Type.SINGLE, emptyList()), listOf(FilterItem("rating","")))
        emit(NetworkRepository.getDirectoryPage(1, bypassModel, FilterRequest(listOf(filter, order))))
    }

    private fun createSection(
        bypassModel: BypassModel,
        title: String,
        query: String,
        limit: Int
    ): SectionData {
        return SectionData(
            title,
            Pager(
                config = PagingConfig(limit),
                pagingSourceFactory = { BestSource(bypassModel, query, limit) }).flow
        )
    }

    override suspend fun customHomeSections(bypassModel: BypassModel): List<SectionData> {
        return listOf(
            createSection(bypassModel, "Featured", "status[]=1&order=rating", 5),
            run {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                createSection(bypassModel, "Best $year", "year[]=$year&order=rating", 15)
            },
            createSection(bypassModel, "Best overall", "order=rating", 15)
        )
    }

    override suspend fun sendReview(
        bypassModel: BypassModel,
        id: Int,
        reviewResult: ReviewResult
    ): Boolean {
        return withContext(Dispatchers.IO){
            NetworkRepository.sendReview(id, reviewResult, bypassModel)
        }
    }

    override suspend fun extraDirectoryPagerData(
        bypassModel: BypassModel,
        request: ExtraDirectoryRequest
    ): PagerData<*, DirectoryModel> {
        val filter = FilterResult(FilterData("genre","",FilterData.Type.SINGLE, emptyList()), listOf(FilterItem(request.payload.orEmpty(),"")))
        val order = FilterResult(FilterData("order","",FilterData.Type.SINGLE, emptyList()), listOf(FilterItem("title","")))
        return PagerData(24, DirectorySource(bypassModel, FilterRequest(listOf(filter,order))))
    }
}