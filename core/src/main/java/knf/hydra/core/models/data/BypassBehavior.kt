package knf.hydra.core.models.data

sealed class BypassBehavior(
    val useLastUA: Boolean = true,
    val showReloadButton: Boolean = false,
    val skipCaptcha: Boolean = false,
    val maxTryCount: Int = 3,
    val clearCookies: Boolean = false,
    val useDialog: Boolean = false,
    val dialogStyle: DialogStyle = DialogStyle.CLASSIC
) {
    class Default : BypassBehavior()
    class Custom(
        useLastUA: Boolean = true,
        showReloadButton: Boolean = false,
        skipCaptcha: Boolean = false,
        maxTryCount: Int = 3,
        clearCookies: Boolean = false,
        useDialog: Boolean = true,
        dialogStyle: DialogStyle = DialogStyle.CLASSIC
    ) : BypassBehavior(useLastUA, showReloadButton, skipCaptcha, maxTryCount, clearCookies, useDialog, dialogStyle)
}

enum class DialogStyle(val value: Int){
    SHEET(0),CLASSIC(1)
}