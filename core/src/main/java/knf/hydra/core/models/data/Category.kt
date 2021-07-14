package knf.hydra.core.models.data

enum class Category(val value: Int){
    UNKNOWN(-1),
    ANIME(0),
    MOVIE(1),
    SERIES(2),
    PORN(3),
    TV(4),
    MANGA(5),
    BOOK(6);
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
    }
}