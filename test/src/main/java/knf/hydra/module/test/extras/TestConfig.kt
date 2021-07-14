package knf.hydra.module.test.extras

import knf.hydra.core.HeadConfig
import knf.hydra.core.models.data.BypassBehavior
import knf.hydra.core.models.data.DialogStyle
import knf.hydra.module.test.decoders.GoCDN

class TestConfig : HeadConfig() {
    init {
        isRecentsAvailable = true
        isDirectoryAvailable = true
        isSearchAvailable = true
        isCalendarEnabled = true
        isNotifyRecentsEnabled = true
        searchBarText = "Search animes"
        bypassBehavior = BypassBehavior.Custom(useLastUA = false, skipCaptcha = true, useDialog = true, dialogStyle = DialogStyle.CLASSIC)
        customDecoders = listOf(
            GoCDN()
        )
    }
}