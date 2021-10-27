package knf.hydra.core.models.data

enum class LayoutType(val value: Int) {
    SINGLE(0), MULTIPLE(1), UNKNOWN(2);
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
    }
}