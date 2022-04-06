package knf.hydra.core.models.data

/**
 * Cloudflare Bypass settings
 */
sealed class BypassBehavior(
    val useLastUA: Boolean = true,
    val showReloadButton: Boolean = false,
    val skipCaptcha: Boolean = false,
    val maxTryCount: Int = 3,
    val clearCookies: Boolean = false,
    val useDialog: Boolean = false,
    val dialogStyle: DialogStyle = DialogStyle.CLASSIC,
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
     * @param showReloadButton Show a reload button, the UserAgent will change each time.
     * @param skipCaptcha Reload every time a captcha is required.
     * @param maxTryCount Max number of redirects before an UserAgent reload.
     * @param clearCookies Clear all cookies each time the bypass is needed.
     * @param useDialog Create bypass with UI
     * @param dialogStyle Type of dialog, Bottom [SHEET][DialogStyle.SHEET] or [CLASSIC][DialogStyle.CLASSIC] dialog.
     * @param isRequired Disable the bypass creation
     */
    class Custom(
        useLastUA: Boolean = true,
        showReloadButton: Boolean = false,
        skipCaptcha: Boolean = false,
        maxTryCount: Int = 3,
        clearCookies: Boolean = false,
        useDialog: Boolean = true,
        dialogStyle: DialogStyle = DialogStyle.CLASSIC,
        isRequired: Boolean = true
    ) : BypassBehavior(useLastUA, showReloadButton, skipCaptcha, maxTryCount, clearCookies, useDialog, dialogStyle, isRequired)
}

/**
 * Bypass dialog style
 */
enum class DialogStyle(val value: Int){
    SHEET(0),CLASSIC(1)
}