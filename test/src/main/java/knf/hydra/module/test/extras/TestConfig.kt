package knf.hydra.module.test.extras

import knf.hydra.core.HeadConfig
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.*
import knf.hydra.module.test.decoders.GoCDN

class TestConfig : HeadConfig() {
    init {
        isRecentsAvailable = true
        isDirectoryAvailable = true
        isSearchAvailable = true
        isCalendarEnabled = true
        isNotifyRecentsEnabled = true
        searchBarText = "Search animes"
        reviewConfig = ReviewConfig(
            starsState = ReviewConfig.State.REQUIRED,
            commentaryState = ReviewConfig.State.DISABLED,
            singleReview = true,
            minRating = 1.0
        )
        bypassBehavior = BypassBehavior.Custom(
            useLastUA = false,
            skipCaptcha = true,
            useDialog = true,
            dialogStyle = DialogStyle.CLASSIC
        )
        analyticsSettings = Analytics.defaultSettings(Analytics.Type.TAG)
        customDecoders = listOf(
            GoCDN()
        )
        settingsPage = SettingPage(
            listOf(
                Section(
                    "Información de MAL",
                    listOf(
                        Setting.Switch("Datos basicos", "Año,y trailer", "mal_basic_data", false),
                        Setting.Switch("Staff", "Staff de producción y seiyus", "mal_staff", false),
                        Setting.Switch("Galería", "Imágenes y videos relacionados", "mal_gallery", false),
                        Setting.Switch("Música", "Música relacionada", "mal_music", false)
                    )
                )
            )
        )
    }
}