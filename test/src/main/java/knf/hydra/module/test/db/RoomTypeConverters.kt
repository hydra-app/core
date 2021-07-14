package knf.hydra.module.test.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters

class RoomTypeConverters {
    @TypeConverter
    fun stringListToString(list: List<String>): String = list.joinToString { ":,:" }
    @TypeConverter
    fun stringToStringList(text: String): List<String> = text.split(":,:")
}