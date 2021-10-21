package knf.hydra.core

import knf.hydra.core.models.DirectoryModel
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.BypassBehavior
import knf.hydra.core.models.data.ReviewConfig
import knf.hydra.core.models.data.SettingPage
import knf.hydra.core.models.data.VideoDecoder

/**
 * Module configuration
 **/
abstract class HeadConfig {
    var isRecentsAvailable: Boolean = false
    var isDirectoryAvailable: Boolean = false
    var isSearchAvailable: Boolean = false
    var isSearchSuggestionsAvailable:Boolean = false
    var isCalendarEnabled: Boolean = false
    var isNotifyRecentsEnabled = false
    var reviewConfig: ReviewConfig? = null
    var searchBarText: String? = null
    var customDecoders: List<VideoDecoder>? = null
    var bypassBehavior: BypassBehavior = BypassBehavior.Default()
    var analyticsOptions: Analytics.Options? = null
    var directoryOrientation: DirectoryModel.Orientation = DirectoryModel.Orientation.VERTICAL
    var settingsPage: SettingPage? = null
}