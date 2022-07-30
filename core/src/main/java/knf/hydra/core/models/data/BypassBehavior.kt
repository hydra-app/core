/*
 * Created by @UnbarredStream on 30/07/22 13:36
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 30/07/22 12:50
 */

package knf.hydra.core.models.data

/**
 * Cloudflare Bypass settings
 */
sealed class BypassBehavior(
    val useLastUA: Boolean = true,
    val skipCaptcha: Boolean = false,
    val maxTryCount: Int = 3,
    val clearCookies: Boolean = false,
    val displayType: DisplayType = DisplayType.ACTIVITY(true),
    val isRequired: Boolean = true
) {
    /**
     * Default implementation
     */
    class Default : BypassBehavior()

    /**
     * Bypass disabled
     */
    class Disabled: BypassBehavior(isRequired = false)

    /**
     * Custom implementation
     *
     * @param useLastUA Remember last UserAgent.
     * @param skipCaptcha Reload every time a captcha is required.
     * @param maxTryCount Max number of redirects before an UserAgent reload.
     * @param clearCookies Clear all cookies each time the bypass is needed.
     * @param displayType Display type of the bypass screen
     * @param isRequired Disable the bypass creation
     */
    class Custom(
        useLastUA: Boolean = true,
        skipCaptcha: Boolean = false,
        maxTryCount: Int = 3,
        clearCookies: Boolean = false,
        displayType: DisplayType = DisplayType.ACTIVITY(true),
        isRequired: Boolean = true
    ) : BypassBehavior(useLastUA, skipCaptcha, maxTryCount, clearCookies, displayType, isRequired)
}

/**
 * Bypass display type
 */
sealed class DisplayType(val value: Int, val showReloadButton: Boolean = false, val style: DialogStyle = DialogStyle.CLASSIC){
    class ACTIVITY(showReloadButton: Boolean): DisplayType(0, showReloadButton)
    class DIALOG(style: DialogStyle): DisplayType(1, style = style)
    object BACKGROUND: DisplayType(2)
}

/**
 * Bypass dialog style
 */
enum class DialogStyle(val value: Int){
    SHEET(0),CLASSIC(1)
}