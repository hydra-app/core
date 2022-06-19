/*
 * Created by @UnbarredStream on 19/06/22 13:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 19/06/22 13:36
 */

package knf.hydra.module.test.extras

import androidx.paging.PagingData
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.DirectoryModel
import knf.hydra.module.test.db.DB
import knf.hydra.module.test.models.TestDirectoryModel
import knf.hydra.module.test.retrofit.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*

object CalendarManager {
    private val requestsChannel = Channel<Request>(Channel.Factory.UNLIMITED)
    private val calendarList = mapOf<Int, MutableList<TestDirectoryModel>>(
        Calendar.SUNDAY to mutableListOf(),
        Calendar.MONDAY to mutableListOf(),
        Calendar.TUESDAY to mutableListOf(),
        Calendar.WEDNESDAY to mutableListOf(),
        Calendar.THURSDAY to mutableListOf(),
        Calendar.FRIDAY to mutableListOf(),
        Calendar.SATURDAY to mutableListOf()
    )

    init {
        GlobalScope.launch(Dispatchers.IO) {
            for (request in requestsChannel) {
                if (calendarList.values.sumOf { it.size } == 0) {
                    var page = 1
                    var hasMore = true
                    while (hasMore) {
                        try {
                            val list = NetworkRepository.getCalendarPage(page, request.bypassModel)
                            list.forEach { item ->
                                try {
                                    val doc =
                                        Jsoup.connect(item.infoLink).headers(request.bypassModel.asMap(NetworkRepository.defaultCookies)).get()
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
                    DB.INSTANCE.calendarDao().nuke()
                    calendarList.forEach { entry ->
                        entry.value.onEach { it.releaseDay = entry.key }
                        DB.INSTANCE.calendarDao().insertAll(entry.value)
                    }
                }
                request.callbackFlow.tryEmit(PagingData.from(calendarList[request.day]?: emptyList()))
            }
        }
    }

    fun request(request: Request) = requestsChannel.trySend(request)

    data class Request(val bypassModel: BypassModel, val day: Int, val callbackFlow: MutableSharedFlow<PagingData<DirectoryModel>>)
}