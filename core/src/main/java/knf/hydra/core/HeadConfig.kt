package knf.hydra.core

import knf.hydra.core.models.data.BypassBehavior
import knf.hydra.core.models.data.VideoDecoder

abstract class HeadConfig {

    var isRecentsAvailable: Boolean = false
    var isDirectoryAvailable: Boolean = false
    var isSearchAvailable: Boolean = false
    var isSearchSuggestionsAvailable:Boolean = false
    var isCalendarEnabled: Boolean = false
    var isNotifyRecentsEnabled = false
    var searchBarText: String? = null
    var customDecoders: List<VideoDecoder>? = null
    var bypassBehavior: BypassBehavior = BypassBehavior.Default()

}