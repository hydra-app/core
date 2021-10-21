package knf.hydra.core.models.data

import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.RecentModel

sealed class NotifyData {
    data class Request(val bypassModel: BypassModel, val lastRecent: RecentModel.Notify?)
    data class Response(val lastRecent: RecentModel?, val list: List<RecentModel>)
}