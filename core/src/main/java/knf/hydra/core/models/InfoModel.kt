package knf.hydra.core.models

import android.os.Parcelable
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import knf.hydra.core.models.data.*
import kotlinx.parcelize.Parcelize
import org.json.JSONObject


abstract class InfoModel {
    abstract var id: Int
    abstract var name: String
    abstract var link: String
    abstract var category: Category

    fun getMin() = InfoModelMin(id, name, link, category, layoutType, coverImage)
    fun isValid() = name.isNotBlank() && link.isNotBlank()

    @Ignore
    open var layoutType: LayoutType = if (category in listOf(Category.PORN, Category.MOVIE)) LayoutType.SINGLE else LayoutType.MULTIPLE

    @Ignore
    open var chaptersData: ChaptersData? = null

    @Ignore
    open var coverImage: String? = null

    @Ignore
    open var userData: UserData? = null

    @Ignore
    open var description: String? = null

    @Ignore
    open var type: String? = null

    @Ignore
    open var state: StateData? = null

    @Ignore
    open var genres: List<Tag>? = null

    @Ignore
    open var tags: List<Tag>? = null

    @Ignore
    open var related: List<Related>? = null

    @Ignore
    open var ranking: RankingData? = null

    @Ignore
    open var extraSections: List<ExtraSection> = emptyList()

    data class Tag(
        val name: String,
        val image: String? = null,
        val payload: String? = null,
        val tagListEnabled: Boolean = false
    ) {
        fun toJson(): String = JSONObject().apply {
            put("name", name)
            put("image", image.orEmpty())
            put("payload", payload.orEmpty())
            put("tagListEnabled", tagListEnabled)
        }.toString()

        companion object{
            fun fromJson(json: String): Tag {
                val decoded = JSONObject(json)
                return Tag(
                    decoded.getString("name"),
                    decoded.getString("image").ifBlank { null },
                    decoded.getString("payload").ifBlank { null },
                    decoded.getBoolean("tagListEnabled")
                )
            }
        }

        sealed class ClickAction{
            class Info(val infoLink: String): ClickAction()
            class Web(val link: String): ClickAction()
            class DirectoryList(val title: String, val payload: String): ClickAction()
            class Clipboard(val text: String): ClickAction()
        }
    }

    abstract class Related {
        abstract var id: Int
        abstract var name: String
        open var link: String? = null
        open var image: String? = null
        open var relation: String? = null
        open var ranking: RankingData? = null
    }

    @Parcelize
    data class UserData(val name: String? = null, val link: String? = null, val image: String? = null, val subText: String? = null): Parcelable {
        fun isValid(): Boolean = !name.isNullOrBlank() && (link == null || link.isNotBlank())
    }

    data class StateData(val state: Type, val emissionDay: EmissionDay? = null) {
        enum class EmissionDay(val value: Int) {
            SUNDAY(1),
            MONDAY(2),
            TUESDAY(3),
            WEDNESDAY(4),
            THURSDAY(5),
            FRIDAY(6),
            SATURDAY(7),
            UNKNOWN(0);

            companion object {
                fun fromValue(value: Int) =
                    when (value) {
                        1 -> SUNDAY
                        2 -> MONDAY
                        3 -> TUESDAY
                        4 -> WEDNESDAY
                        5 -> THURSDAY
                        6 -> FRIDAY
                        7 -> SATURDAY
                        else -> UNKNOWN
                    }
            }
        }

        enum class Type(val value: Int) {
            EMISSION(0), COMPLETED(1), HIATUS(2), UNKNOWN(3);

            companion object {
                fun fromValue(value: Int) = values().find { it.value == value } ?: UNKNOWN
            }
        }
    }

    class Converters {
        @TypeConverter
        fun relatedToString(list: List<Related>?): String {
            list ?: return ""
            return Gson().toJson(list, object : TypeToken<List<Related>>() {}.type)
        }

        @TypeConverter
        fun stringToRelated(json: String): List<Related>? {
            if (json.isBlank()) return null
            return Gson().fromJson(json, object : TypeToken<List<Related>>() {}.type)
        }

        @TypeConverter
        fun musicToString(list: List<Music>?): String {
            list ?: return ""
            return Gson().toJson(list, object : TypeToken<List<Music>>() {}.type)
        }

        @TypeConverter
        fun stringToMusic(json: String): List<Music>? {
            if (json.isBlank()) return null
            return Gson().fromJson(json, object : TypeToken<List<Music>>() {}.type)
        }

        @TypeConverter
        fun emissionDayToInt(day: StateData.EmissionDay): Int {
            return day.value
        }

        @TypeConverter
        fun intToEmissionDay(value: Int): StateData.EmissionDay {
            return StateData.EmissionDay.fromValue(value)
        }

        @TypeConverter
        fun emissionTypeToInt(type: StateData.Type): Int {
            return type.value
        }

        @TypeConverter
        fun intToEmissionType(value: Int): StateData.Type {
            return StateData.Type.fromValue(value)
        }

        @TypeConverter
        fun categoryToInt(category: Category): Int = category.value

        @TypeConverter
        fun intToCategory(value: Int): Category = Category.fromValue(value)

        @TypeConverter
        fun layoutTypeToInt(layoutType: LayoutType): Int = layoutType.value

        @TypeConverter
        fun intToLayoutType(value: Int): LayoutType = LayoutType.fromValue(value)
    }
}