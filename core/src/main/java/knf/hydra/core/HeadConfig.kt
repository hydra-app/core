/*
 * Created by @UnbarredStream on 08/04/22 18:10
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 18:08
 */

package knf.hydra.core

import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.BypassBehavior
import knf.hydra.core.models.data.ReviewConfig
import knf.hydra.core.models.data.SettingPage
import knf.hydra.core.models.data.VideoDecoder

/**
 * Module configuration, enable and modify the behaviour of some features.
 *
 * To use this class you need to create a new class and extend [HeadConfig] or use a [kotlin object](https://kotlinlang.org/docs/object-declarations.html),
 * then modify the required variables in **init**, for example:
 *
 * ```kotlin
 * object: HeadConfig() {
 *    init {
 *       isRecentsAvailable = true
 *       isSearchAvailable = true
 *       ...
 *    }
 * }
 * ```
 **/
abstract class HeadConfig {

    /**
     * Enable the recents feature, this will call [HeadRepository.recentsPager] in order to get the
     * latest recents.
     *
     * If [isNotifyRecentsEnabled] is enabled, the main app will check the recents periodically
     * and will notify any new element.
     */
    var isRecentsAvailable: Boolean = false

    /**
     * Enable the directory section, this will call [HeadRepository.directoryPager]
     * when the user enters the directory section, this also could be called multiple times
     * if [HeadRepository.directoryFilters] is used.
     */
    var isDirectoryAvailable: Boolean = false

    /**
     * Enable the search feature, this will call [HeadRepository.searchPager]
     * every time a user uses the search feature, this can also include custom filters using
     * [HeadRepository.searchFilters].
     */
    var isSearchAvailable: Boolean = false

    /**
     * Enable custom suggestions for the search feature, this will call
     * [HeadRepository.searchSuggestions] every time an user writes a new query.
     */
    var isSearchSuggestionsAvailable: Boolean = false

    /**
     * Enable the calendar feature, this will call [HeadRepository.calendarList] when the main app
     * loads the "Today" section in Home, this also will be called when the user enters the Calendar
     * Activity.
     *
     * Enable this only if your data source supports a daily release system.
     */
    var isCalendarEnabled: Boolean = false

    /**
     * Enable recents notifications feature, if enabled the main app will call [HeadRepository.lastRecents]
     * periodically in order to get the latest recents.
     *
     * [isRecentsAvailable] needs to be enabled as well.
     */
    var isNotifyRecentsEnabled = false

    /**
     * Enable cast feature if your module uses direct video links.
     */
    var isCastEnabled = false

    /**
     * Enable the review feature in the information page, you can decide to request a star based
     * review, a written review, or both.
     *
     * @see ReviewConfig
     */
    var reviewConfig: ReviewConfig? = null

    /**
     * Set a custom message for the search bar, by default the Module name is used.
     *
     * [isSearchAvailable] needs to be enabled.
     */
    var searchBarText: String? = null

    /**
     * Declare custom video decoders, the Main app already supports:
     * - Fembed
     * - GDrive
     * - GoUnlimited
     * - Mixdrop
     * - Mp4Upload
     * - MStream
     * - SBVideo
     * - StreamTape
     * - Upstream
     * - VideosSH
     * - Vudeo
     * - YourUpload
     * - ZippyShare
     *
     * Direct video files needs to be specified in [SourceItem.needDecoder][knf.hydra.core.models.data.SourceItem.needDecoder]
     */
    var customDecoders: List<VideoDecoder>? = null

    /**
     * Change the behavior and look of the Cloudflare bypass system, default is [BypassBehavior.Default].
     *
     * You can disable the bypass for your module using [BypassBehavior.Disabled].
     *
     * @see BypassBehavior
     */
    var bypassBehavior: BypassBehavior = BypassBehavior.Default()

    /**
     * Enable the recommended system, you can choose between collecting the whole information page or
     * only the Tags.
     *
     * If enabled the Main app will call [HeadRepository.analyticsRecommended] with the list of
     * [Analytics.Event].
     *
     * @see Analytics
     */
    var analyticsSettings: Analytics.Settings? = null

    /**
     * Specify if the directory style needs to be [VERTICAL][DirectoryModel.Orientation.VERTICAL] for portraits,
     * or [HORIZONTAL][DirectoryModel.Orientation.HORIZONTAL] for videos.
     *
     * Default is [VERTICAL][DirectoryModel.Orientation.VERTICAL].
     */
    var directoryOrientation: DirectoryModel.Orientation = DirectoryModel.Orientation.VERTICAL

    /**
     * Specify a list of custom settings, an icon will be visible for the user to enter this settings,
     * this settings will persist in the Main app even if the Module is uninstalled.
     *
     * You can access this preferences from [ModulePreferences][knf.hydra.core.tools.ModulePreferences] object.
     *
     * @see SettingPage
     */
    var settingsPage: SettingPage? = null
}