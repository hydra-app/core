package knf.hydra.core.models.data

/**
 * Categories for items.
 */
enum class Category(val value: Int){
    UNKNOWN(-1),
    ANIME(0),
    MOVIE(1),
    SERIES(2),
    HENTAI(3),
    PORN(4),
    TV(5),
    MANGA(6),
    BOOK(7);
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
    }
}