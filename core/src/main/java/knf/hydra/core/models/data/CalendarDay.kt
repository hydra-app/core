package knf.hydra.core.models.data

import java.util.Calendar


/**
 * Represents a calendar day
 *
 * @property value Value equivalent in [Calendar]
 */
enum class CalendarDay(val value: Int) {
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY),
    SUNDAY(Calendar.SUNDAY);

    companion object {
        /**
         * Gets the equivalent [CalendarDay] from [value]
         *
         * @param value Value equivalent in [Calendar]
         * @return A [CalendarDay]
         */
        fun fromValue(value: Int): CalendarDay? {
            return CalendarDay.values().firstOrNull { it.value == value }
        }
    }
}