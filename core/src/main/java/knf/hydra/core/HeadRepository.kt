package knf.hydra.core

import androidx.paging.PagingData
import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.*
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.*
import kotlinx.coroutines.flow.Flow

abstract class HeadRepository {
    abstract fun infoPage(link: String, bypassModel: BypassModel, bridge: MainDbBridge): Flow<InfoModel?>
    abstract fun sourceData(link: String, bypassModel: BypassModel): Flow<SourceData?>
    abstract suspend fun sourceDataType(link: String, bypassModel: BypassModel): SourceData.Type
    open suspend fun recentsPager(bypassModel: BypassModel): Flow<PagingData<RecentModel>>? = null
    open suspend fun lastRecents(data: NotifyData.Request): NotifyData.Response? = null
    open suspend fun directoryPager(bypassModel: BypassModel, filters: FilterRequest?): Flow<PagingData<DirectoryModel>>? = null
    open suspend fun directoryFilters(bypassModel: BypassModel): List<FilterData>? = null
    open suspend fun searchPager(query: String?, bypassModel: BypassModel, filters: FilterRequest?): Flow<PagingData<SearchModel>>? = null
    open suspend fun searchFilters(bypassModel: BypassModel): List<FilterData>? = null
    open suspend fun searchSuggestions(query: String, bypassModel: BypassModel): List<String>? = null
    open suspend fun calendarList(bypassModel: BypassModel, day: Int = -1): Flow<CalendarList?>? = null
    open suspend fun customHomeSections(bypassModel: BypassModel): List<SectionData> = emptyList()
    open suspend fun analyticsRecommended(bypassModel: BypassModel, events: List<Analytics.Event>): List<DirectoryModel>? = null
    open suspend fun sendReview(bypassModel: BypassModel, id: Int, reviewResult: ReviewResult):Boolean? = null
    open suspend fun tagPager(bypassModel: BypassModel, tag: InfoModel.Tag): Flow<PagingData<DirectoryModel>>? = null
    open suspend fun infoExtraDataDirectoryList(bypassModel: BypassModel, payload: String): Flow<PagingData<DirectoryModel>>? = null
    open suspend fun userProfile(bypassModel: BypassModel, userData: InfoModel.UserData): Flow<UserProfileModel?>? = null
}