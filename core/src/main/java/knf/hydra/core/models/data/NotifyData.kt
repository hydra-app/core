package knf.hydra.core.models.data

import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.RecentModel
import knf.hydra.core.models.data.NotifyData.Request
import knf.hydra.core.models.data.NotifyData.Response

/**
 * Base notify data
 * @see Request
 * @see Response
 */
sealed class NotifyData {
    /**
     * Notification request
     *
     * @property bypassModel The bypass data for the module
     * @property lastRecent The last item notified for the module
     */
    data class Request(val bypassModel: BypassModel, val lastRecent: RecentModel.Notify?)

    /**
     * Notifications response
     *
     * @property lastRecent The last recent to remember for the next request
     * @property list The items to be notified
     */
    data class Response(val lastRecent: RecentModel?, val list: List<RecentModel>)
}