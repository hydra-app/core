/*
 * Created by @UnbarredStream on 30/07/22 13:36
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 30/07/22 12:54
 */

package knf.hydra.module.test.extras

import knf.hydra.core.HeadConfig
import knf.hydra.core.models.analytics.Analytics
import knf.hydra.core.models.data.BypassBehavior
import knf.hydra.core.models.data.DialogStyle
import knf.hydra.core.models.data.DisplayType
import knf.hydra.core.models.data.ReviewConfig
import knf.hydra.core.models.data.Section
import knf.hydra.core.models.data.Setting
import knf.hydra.core.models.data.SettingPage
import knf.hydra.module.test.decoders.GoCDN

class TestConfig : HeadConfig() {
    init {
        isRecentsAvailable = true
        isDirectoryAvailable = true
        isSearchAvailable = true
        isCalendarEnabled = true
        isNotifyRecentsEnabled = true
        isCastEnabled = true
        searchBarText = "Buscar animes"
        reviewConfig = ReviewConfig(
            starsState = ReviewConfig.State.REQUIRED,
            commentaryState = ReviewConfig.State.DISABLED,
            singleReview = true,
            minRating = 1.0
        )
        bypassBehavior = BypassBehavior.Custom(
            useLastUA = false,
            skipCaptcha = true,
            displayType = DisplayType.DIALOG(DialogStyle.SHEET)
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
                        Setting.Switch("Datos basicos", "Año,y trailer", "mal_basic_data", true),
                        Setting.Switch("Staff", "Staff de producción y seiyus", "mal_staff", true),
                        Setting.Switch("Galería", "Imágenes y videos relacionados", "mal_gallery", true),
                        Setting.Switch("Música", "Música relacionada", "mal_music", true)
                    )
                )
            )
        )
    }
}