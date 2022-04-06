package knf.hydra.core.models.analytics

import androidx.room.Entity
import knf.hydra.core.HeadConfig
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.analytics.Analytics.Action.*
import knf.hydra.core.models.analytics.Analytics.Type.INFO
import knf.hydra.core.models.analytics.Analytics.Type.TAG
import org.json.JSONObject

/**
 * Analytics object
 *
 * @see HeadRepository.analyticsRecommended
 * @see HeadConfig.analyticsSettings
 */
object Analytics {

    /** @suppress */
    @Entity(tableName = "AnalyticEvents", primaryKeys = ["id", "module"])
    data class Event(val id: Int, val module: String, val payload: String?, var score: Int){
        /**
         * Represents an event, with the given [id] and **optional** [payload].
         */
        data class Info(val id: Int, val payload: String?)
    }

    /**
     * Type of events the main app will register, [INFO] or [TAG].
     */
    enum class Type { INFO, TAG }

    /**
     * Actions the main app can register.
     * - [OPEN_INFO]
     * - [FROM_SEARCH]
     * - [ADD_FAV]
     * - [REMOVE_FAV]
     * - [OPEN_CONTENT]
     * - [DOWNLOAD_CONTENT]
     */
    enum class Action {
        OPEN_INFO,
        FROM_SEARCH,
        ADD_FAV,
        REMOVE_FAV,
        OPEN_CONTENT,
        DOWNLOAD_CONTENT
    }

    /**
     * Default scores for the [Action]
     */
    object DefaultScores{
        const val OPEN_INFO = 1
        const val FROM_SEARCH = 1
        const val ADD_FAV = 2
        const val REMOVE_FAV = -2
        const val OPEN_CONTENT = 1
        const val DOWNLOAD_CONTENT = 1
    }

    /**
     * Default keys to register a [Type.INFO] event.
     */
    object DefaultKeys {
        const val KEY_LINK = "link"
        const val KEY_CATEGORY = "category"
        const val KEY_GENRES = "genres"
        const val KEY_TAGS = "tags"
    }

    /**
     * Customizable Analytics settings.
     *
     * @property type The [Type] of data the main app will process.
     * @property scores A map of [Action] with the desired scores to add or remove.
     * @property eventCreator A factory to convert [InfoModel] or [InfoModel.Tag] to [Event.Info] payload.
     */
    data class Settings(val type: Type, val scores: Map<Action, Int>, val eventCreator: (Any) -> Event.Info?)

    /**
     * Convenient function to get default settings using [DefaultScores].
     *
     * In case of [Type.INFO] the [InfoModel] link, category, genres and tags will be converted to Json and will be used as the [Event] payload.
     *
     * @param type The default [Type] to process.
     */
    fun defaultSettings(type: Type) = Settings(
        type,
        mapOf(
            OPEN_INFO to DefaultScores.OPEN_INFO,
            FROM_SEARCH to DefaultScores.FROM_SEARCH,
            ADD_FAV to DefaultScores.ADD_FAV,
            REMOVE_FAV to DefaultScores.REMOVE_FAV,
            OPEN_CONTENT to DefaultScores.OPEN_CONTENT,
            DOWNLOAD_CONTENT to DefaultScores.DOWNLOAD_CONTENT
        ),
        if (type == TAG)
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