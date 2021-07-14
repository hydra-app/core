package knf.hydra.core.main

import knf.hydra.core.models.RecentModel

abstract class MainDbBridge {
    abstract fun isChapterSeen(id: Int,module: String): Boolean
    abstract fun getLastRecent(module: String): RecentModel.Notify?
    abstract fun saveLastRecent(model: RecentModel.Notify)
}