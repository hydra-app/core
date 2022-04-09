/*
 * Created by @UnbarredStream on 08/04/22 19:35
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 19:33
 */

package knf.hydra.core

import androidx.paging.PagingData
import knf.hydra.core.models.*
import knf.hydra.core.models.InfoModel.Tag
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Module repository, it contains all the methods required for the Main app to get data from the Module,
 * abstract functions [infoPage] and [sourceData] are **required**, all other functions are **optional**.
 *
 * <b>Sample usage</b>
 *
 * ```kotlin
 * class Repository : HeadRepository(){
 *
 *    override fun infoPage(link: String, bypassModel: BypassModel): Flow<InfoModel?> {
 *        return flow {
 *            emit(null)
 *        }
 *    }
 *
 *    override fun sourceData(link: String, bypassModel: BypassModel): Flow<SourceData?> {
 *        return flow { emit(null) }
 *    }
 *
 *    override suspend fun recentsPager(bypassModel: BypassModel): Flow<PagingData<RecentModel>> {
 *        return Pager(
 *            config = PagingConfig(
 *                pageSize = 20,
 *                enablePlaceholders = false
 *            ),
 *            pagingSourceFactory = { RecentsSource() }
 *        ).flow
 *    }
 * }
 * ```
 */
abstract class HeadRepository {
    /**
     * This function is called when loading the information page for any of this items:
     * - [RecentModel.infoLink]
     * - [DirectoryModel.infoLink]
     *
     * It's required that this 3 properties have the same link.
     *
     * @param link Information link, it can be any string, if your module need a content id or something
     * like that you can send it as the link, it's not required to be an http link.
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A [flow](https://developer.android.com/kotlin/flow#create) containing the [InfoModel]
     * for this [link] or null if there was an error.
     */
    abstract fun infoPage(link: String, bypassModel: BypassModel): Flow<InfoModel?>

    /**
     * This function is called when loading the sources for a [ContentItemModel] it can be a [VideoSource],
     * [GallerySource] or a [WebSource].
     *
     * @param link The link from [ContentItemModel.itemLink] declared in [InfoModel.contentData]
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A [flow](https://developer.android.com/kotlin/flow#create) containing the [SourceData]
     * for this [link] or null if there was an error.
     */
    abstract fun sourceData(link: String, bypassModel: BypassModel): Flow<SourceData?>

    /**
     * This function is called only if [HeadConfig.isRecentsAvailable] is enabled, it's used for
     * loading the recents section in Home.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A [PagingData flow](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingdata-stream)
     * for the recents.
     */
    open suspend fun recentsPager(bypassModel: BypassModel): Flow<PagingData<RecentModel>>? = null

    /**
     * This function is called only if [HeadConfig.isRecentsAvailable] and [HeadConfig.isNotifyRecentsEnabled]
     * are enabled, it's used to get the latest sublist of recents.
     *
     * The system works like this:
     * - Every X minutes the app will call this method with a [NotifyData.Request], it includes the
     * latest [BypassModel] for the module and the last [RecentModel] send in a [NotifyData.Request]
     * or null if it's the first request.
     * - The module needs to load the latest recents until the item in the request and send them in a
     * [NotifyData.Response], the response needs to include the new latest recent.
     *
     * ## **Notes:**
     * - The Main app won't notify the first time it sends a request so if the item in the request
     * is null you can send a response only containing the latest recent.
     * - If the latest in the Response is null, the app will send the same latest in the next request.
     * - The amount of notifications is limited to 5 per request, if the response contains more than 5
     * recents the Main app will only take the first 5 elements.
     *
     * @param data The request for latest recents
     * @return The response containing the list of latest recents and the latest recent to record
     */
    open suspend fun lastRecents(data: NotifyData.Request): NotifyData.Response? = null

    /**
     * This function is called only if [HeadConfig.isDirectoryAvailable] is enabled, it's used to load the directory of items.
     *
     * This section can be filtered if [directoryFilters] is declared.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param filters Requested filters by the user.
     * @return A [PagingData flow](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingdata-stream)
     * for the directory.
     */
    open suspend fun directoryPager(bypassModel: BypassModel, filters: FilterRequest?): Flow<PagingData<DirectoryModel>>? = null

    /**
     * This function is used to declare the custom filters to be used in [directoryPager].
     *
     * See [FilterData] for more information.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A list of [FilterData] that represents the filters available for the directory.
     */
    open suspend fun directoryFilters(bypassModel: BypassModel): List<FilterData>? = null

    /**
     * This function is called only if [HeadConfig.isSearchAvailable] is enabled, it's used to search items in the module.
     *
     * This section can be filtered if [searchFilters] is declared.
     *
     * @param query The query to search
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param filters Requested filters by the user.
     * @return A [PagingData flow](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingdata-stream)
     * for the search result.
     */
    open suspend fun searchPager(query: String?, bypassModel: BypassModel, filters: FilterRequest?): Flow<PagingData<DirectoryModel>>? = null

    /**
     * This function is used to declare the custom filters to be used in [searchPager].
     *
     * See [FilterData] for more information.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return Requested filters by the user.
     */
    open suspend fun searchFilters(bypassModel: BypassModel): List<FilterData>? = null

    /**
     * This function is called only if [HeadConfig.isSearchSuggestionsAvailable] is enabled, it's used for search autocompletion.
     *
     * @param query The current query to search
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A list of possible titles based on the query
     */
    open suspend fun searchSuggestions(query: String, bypassModel: BypassModel): List<String>? = null

    /**
     * This function is called only if [HeadConfig.isCalendarEnabled] is enabled, it's used to create a weekly base calendar.
     *
     * If the calendar is enabled home will try to load a "Today" list calling this function with the current [day] as parameter.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param day The day requested by the main app using [Calendar.DAY_OF_WEEK], or -1 if loading the full list.
     * @return A [flow](https://developer.android.com/kotlin/flow#create) containing a [CalendarList] object.
     */
    open suspend fun calendarList(bypassModel: BypassModel, day: Int = -1): Flow<CalendarList?>? = null

    /**
     * Declare custom Home sections
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @return A list of [SectionData].
     */
    open suspend fun customHomeSections(bypassModel: BypassModel): List<SectionData> = emptyList()

    /**
     * This function is called only if [HeadConfig.analyticsSettings] is declared, it's used to load a list of recommendations based on the user behaviour and the
     * [module configuration][HeadConfig.analyticsSettings].
     *
     * The main app needs to register at least 3 events to start recommending.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param events The list of the top 3-15 events sorted by [Analytics.Event.score].
     * @return A [flow](https://developer.android.com/kotlin/flow#create) containing a list of [DirectoryModel] with the recommendations.
     */
    open suspend fun analyticsRecommended(bypassModel: BypassModel, events: List<Analytics.Event>): Flow<List<DirectoryModel>>? = null

    /**
     * This function is called only if [HeadConfig.reviewConfig] is declared, it's called whe the user sends a review.
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param id The id of the reviewed [InfoModel].
     * @param reviewResult The review send by the user.
     * @return True if the review was processed successfully.
     */
    open suspend fun sendReview(bypassModel: BypassModel, id: Int, reviewResult: ReviewResult):Boolean? = null

    /**
     * Load a filtered list of the directory based on the payload from [ClickAction.ExtraDirectory] or [Tag.payload]
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param request The requested payload to load.
     * @return A [PagingData flow](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data#pagingdata-stream)
     * for the items in the request.
     */
    open suspend fun extraDirectoryPager(bypassModel: BypassModel, request: ExtraDirectoryRequest): Flow<PagingData<DirectoryModel>>? = null

    /**
     * Load a profile with custom sections declared in the [ProfileModel].
     *
     * @param bypassModel Cloudflare bypass information extracted by the Main app, if your module
     * doesn't require a bypass you can disable it in [HeadConfig.bypassBehavior].
     * @param profileData The declared [InfoModel.ProfileData] from [InfoModel].
     * @return A [flow](https://developer.android.com/kotlin/flow#create) containing the profile from the declared [InfoModel.ProfileData].
     */
    open suspend fun userProfile(bypassModel: BypassModel, profileData: InfoModel.ProfileData): Flow<ProfileModel?>? = null
}