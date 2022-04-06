package knf.hydra.core.models.data

/**
 * Layout type for the information page.
 * - [SINGLE] if there is only 1 item for the source (e.g. Movies)
 * - [MULTIPLE] if there are more than 1 item for the source (e.g. Series)
 */
enum class LayoutType(val value: Int) {
    SINGLE(0), MULTIPLE(1), UNKNOWN(2);
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
    }
}