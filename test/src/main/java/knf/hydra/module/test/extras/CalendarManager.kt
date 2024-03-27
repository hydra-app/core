/*
 * Created by @UnbarredStream on 19/06/22 13:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 19/06/22 13:36
 */

package knf.hydra.module.test.extras

import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.data.CalendarDay
import knf.hydra.module.test.models.TestDirectoryModel
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object CalendarManager {
    private val calendarList = mapOf<Int, MutableList<TestDirectoryModel>>(
        Calendar.SUNDAY to mutableListOf(),
        Calendar.MONDAY to mutableListOf(),
        Calendar.TUESDAY to mutableListOf(),
        Calendar.WEDNESDAY to mutableListOf(),
        Calendar.THURSDAY to mutableListOf(),
        Calendar.FRIDAY to mutableListOf(),
        Calendar.SATURDAY to mutableListOf()
    )
    private var isLoaded = false
    private var isLoading = false

    suspend fun getDay(bypassModel: BypassModel, day: CalendarDay): List<TestDirectoryModel> {
        if (isLoaded) {
            return calendarList.getValue(day.value)
        } else {
            if (isLoading) {
                while (isLoading) {
                    delay(1000)
                }
                return getDay(bypassModel, day)
            } else {
                isLoading = true
            }
            var page = 1
            var hasMore = true
            while (hasMore) {
                try {
                    val list = NetworkRepository.getCalendarPage(page, bypassModel)
                    list.forEach { item ->
                        try {
                            val doc =
                                Jsoup.connect(item.infoLink).headers(bypassModel.asMap(NetworkRepository.defaultCookies)).get()
                            val html = doc.html()
                            val info =
                                "anime_info = \\[(.*)\\];".toRegex()
                                    .find(html)?.destructured?.component1()
                                    ?.split(",")?.map { it.replace("\"", "") }
                            if (info?.size!! >= 4) {
                                val calendar = Calendar.getInstance().apply {
                                    SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.getDefault()
                                    ).parse(info.last())
                                        ?.let {
                                            time = it
                                        }
                                }
                                calendarList[calendar.get(Calendar.DAY_OF_WEEK)]?.add(item)
                            }
                        } catch (e: Exception) {
                            //e.printStackTrace()
                        }
                    }
                    hasMore = list.size == 24
                } catch (e: Exception) {
                    e.printStackTrace()
                    //
                }
                page++
            }
            isLoaded = true
            isLoading = false
            return getDay(bypassModel, day)
        }
    }
}