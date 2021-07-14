package knf.hydra.core.models

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.Size
import androidx.paging.PagingData
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.ExtraData
import knf.hydra.core.models.data.RankingData
import kotlinx.coroutines.flow.Flow


abstract class InfoModel {
    abstract var id: Int
    abstract var name: String
    abstract var link: String
    abstract var category: Category

    fun getMin() = InfoModelMin(id, name, link, category, coverImage)

    @Ignore
    open var chaptersPaging: Flow<PagingData<ChapterModel>>? = null

    @Ignore
    open var coverImage: String? = null

    @Ignore
    open var description: String? = null

    @Ignore
    open var type: String? = null

    @Ignore
    open var state: StateData? = null

    @Ignore
    open var genres: List<String>? = null

    @Ignore
    open var tags: List<String>? = null

    @Ignore
    open var related: List<Related>? = null

    @Ignore
    open var ranking: RankingData? = null

    @Ignore
    open var music: Flow<List<Music>?>? = null

    @Ignore
    open var youtubeTrailer: String? = null

    @Ignore
    open var extraData: List<ExtraData> = emptyList()

    data class Music(
        var title: String,
        var link: String,
        var subtitle: String? = null
    )

    abstract class Related {
        abstract var id: Int
        abstract var name: String
        open var link: String? = null
        open var image: String? = null
        open var relation: String? = null
        open var ranking: RankingData? = null
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
                fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
            }
        }
    }

    class Converters{
        @TypeConverter
        fun relatedToString(list: List<Related>?): String {
            list?:return ""
            return Gson().toJson(list, object : TypeToken<List<Related>>() {}.type)
        }

        @TypeConverter
        fun stringToRelated(json:String): List<Related>? {
            if (json.isBlank()) return null
            return Gson().fromJson(json, object : TypeToken<List<Related>>() {}.type)
        }

        @TypeConverter
        fun musicToString(list: List<Music>?): String {
            list?:return ""
            return Gson().toJson(list, object : TypeToken<List<Music>>() {}.type)
        }

        @TypeConverter
        fun stringToMusic(json:String): List<Music>? {
            if (json.isBlank()) return null
            return Gson().fromJson(json, object : TypeToken<List<Music>>() {}.type)
        }

        @TypeConverter
        fun emissionDayToInt(day: StateData.EmissionDay): Int{
            return day.value
        }

        @TypeConverter
        fun intToEmissionDay(value: Int): StateData.EmissionDay{
            return StateData.EmissionDay.fromValue(value)
        }

        @TypeConverter
        fun emissionTypeToInt(type: StateData.Type): Int{
            return type.value
        }

        @TypeConverter
        fun intToEmissionType(value: Int): StateData.Type{
            return StateData.Type.fromValue(value)
        }

        @TypeConverter
        fun categoryToInt(category: Category): Int = category.value

        @TypeConverter
        fun intToCategory(value: Int): Category = Category.fromValue(value)
    }
}