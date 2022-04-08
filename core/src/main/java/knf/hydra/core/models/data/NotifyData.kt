/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

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