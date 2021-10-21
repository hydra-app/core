package knf.hydra.core.models.analytics

import androidx.room.Entity
import knf.hydra.core.models.InfoModel
import org.json.JSONObject

object Analytics {

    @Entity(tableName = "AnalyticEvents", primaryKeys = ["id", "module"])
    data class Event(val id: Int, val module: String, val payload: String?, var score: Int){
        data class Info(val id: Int, val payload: String?)
    }

    enum class Type { INFO, TAG }

    enum class Action {
        OPEN_INFO,
        FROM_SEARCH,
        ADD_FAV,
        REMOVE_FAV,
        OPEN_CHAPTER,
        DOWNLOAD_CHAPTER
    }
    object DefaultScores{
        const val OPEN_INFO = 1
        const val FROM_SEARCH = 1
        const val ADD_FAV = 2
        const val REMOVE_FAV = -2
        const val OPEN_CHAPTER = 1
        const val DOWNLOAD_CHAPTER = 1
    }
    object DefaultKeys {
        const val KEY_LINK = "link"
        const val KEY_CATEGORY = "category"
        const val KEY_GENRES = "genres"
        const val KEY_TAGS = "tags"
    }
    data class Options(val type: Type, val scores: Map<Action, Int>, val eventCreator: (Any) -> Event.Info?)

    fun defaultOptions(type: Type) = Options(
        type,
        mapOf(
            Action.OPEN_INFO to DefaultScores.OPEN_INFO,
            Action.FROM_SEARCH to DefaultScores.FROM_SEARCH,
            Action.ADD_FAV to DefaultScores.ADD_FAV,
            Action.REMOVE_FAV to DefaultScores.REMOVE_FAV,
            Action.OPEN_CHAPTER to DefaultScores.OPEN_CHAPTER,
            Action.DOWNLOAD_CHAPTER to DefaultScores.DOWNLOAD_CHAPTER
        ),
        if (type == Type.TAG)
            { model -> (model as? InfoModel.Tag)?.let { Event.Info(it.name.hashCode(), it.payload) } }
        else
            { model ->
                (model as? InfoModel)?.let { infoModel ->
                    val payload = JSONObject().apply {
                        put(DefaultKeys.KEY_LINK, infoModel.link)
                        put(DefaultKeys.KEY_CATEGORY, infoModel.category.value)
                        put(DefaultKeys.KEY_GENRES, infoModel.genres?.joinToString { it.name })
                        put(DefaultKeys.KEY_TAGS, infoModel.tags?.joinToString { it.name })
                    }
                    Event.Info(infoModel.id, payload.toString())
                }
            }
    )
}