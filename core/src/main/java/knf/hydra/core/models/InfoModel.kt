package knf.hydra.core.models

import android.os.Parcelable
import androidx.room.Ignore
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.data.*
import kotlinx.parcelize.Parcelize

/** Represents the info of a directory item */
abstract class InfoModel {
    /** Unique id for the item, for example the hash of the [link] */
    abstract var id: Int

    /** Item name */
    abstract var name: String

    /** Item info link */
    abstract var link: String

    /** Content category */
    abstract var category: Category

    /** @suppress */
    fun getMin() = InfoModelMin(id, name, link, category, layoutType, coverImage)

    /** @suppress */
    fun isValid() = name.isNotBlank() && link.isNotBlank()

    @Ignore
    /**
     * Layout type to be used when loading the info, [LayoutType.SINGLE] for single items like [Category.MOVIE], or [LayoutType.MULTIPLE]
     * for multiple items like [Category.SERIES], by default the system uses the [category] to decide wich one to use
     */
    open var layoutType: LayoutType = if (category in listOf(Category.PORN, Category.MOVIE)) LayoutType.SINGLE else LayoutType.MULTIPLE

    @Ignore
    /** Data representing the content of this item */
    open var contentData: ContentData? = null

    @Ignore
    /** Optional cover image for this item */
    open var coverImage: String? = null

    @Ignore
    /** Optional profile data to be shown */
    open var profileData: ProfileData? = null

    @Ignore
    /** Optional description for the item */
    open var description: String? = null

    @Ignore
    /** Optional item type */
    open var type: String? = null

    @Ignore
    /** Optional item state */
    open var state: StateData? = null

    @Ignore
    /** Optional item genres */
    open var genres: List<Tag>? = null

    @Ignore
    /** Optional item tags */
    open var tags: List<Tag>? = null

    @Ignore
    /** Optional related items */
    open var related: List<Related>? = null

    @Ignore
    /** Optional ranking data */
    open var ranking: RankingData? = null

    @Ignore
    /** Optional extra sections */
    open var extraSections: List<ExtraSection> = emptyList()

    /**
     * Represents a tag
     *
     * @property name Tag name
     * @property image Optional tag image
     * @property payload Optional payload
     * @property clickAction Optional click action
     */
    data class Tag(
        val name: String,
        val image: String? = null,
        val payload: String? = null,
        val clickAction: ClickAction? = null
    )

    /** Represents a related item */
    abstract class Related {
        /** Unique id for the item, for example the hash of the [link] */
        abstract var id: Int

        /** Item name */
        abstract var name: String

        /** Optional info link used to open the [Info page][HeadRepository.infoPage] */
        open var link: String? = null

        /** Optional related item image */
        open var image: String? = null

        /** Optional relation subtext*/
        open var relation: String? = null

        /** Optional ranking data */
        open var ranking: RankingData? = null
    }

    /**
     * Represents a profile data
     *
     * @property name Profile name
     * @property link Profile link
     * @property image Profile image
     * @property subText Profile subtext
     */
    @Parcelize
    data class ProfileData(
        val name: String? = null,
        val link: String? = null,
        val image: String? = null,
        val subText: String? = null
    ) : Parcelable {
        /** @suppress */
        fun isValid(): Boolean = !name.isNullOrBlank() && (link == null || link.isNotBlank())
    }

    /**
     * Represents the item state
     *
     * @property state Item state
     * @property emissionDay Optional release day
     */
    data class StateData(val state: Type, val emissionDay: EmissionDay? = null) {
        /** Day of emission */
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
                /** @suppress */
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

        /** State type */
        enum class Type(val value: Int) {
            EMISSION(0), COMPLETED(1), HIATUS(2), UNKNOWN(3);

            companion object {
                fun fromValue(value: Int) = values().find { it.value == value } ?: UNKNOWN
            }
        }
    }

    /** @suppress */
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