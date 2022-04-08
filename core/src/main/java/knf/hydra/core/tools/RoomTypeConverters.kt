/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.tools

import androidx.room.TypeConverter
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.ExtraData
import knf.tools.gsonktx.GSON
import knf.tools.gsonktx.toJson

/** @suppress */
class RoomTypeConverters {
    @TypeConverter
    fun stringListToString(list: List<String>?): String? = list?.joinToString { ":,:" }
    @TypeConverter
    fun stringToStringList(text: String?): List<String>? = text?.split(":,:")
    @TypeConverter
    fun relatedListToJson(list: List<InfoModel.Related>?): String? = list?.toJson()
    @TypeConverter
    fun jsonToRelatedList(json:String?): List<InfoModel.Related>? = json?.let { GSON.fromJson(json) }
    @TypeConverter
    fun extraDataToJson(data:ExtraData?): String? = data?.toJson()
    @TypeConverter
    fun jsonToExtraData(json:String?): ExtraData? = json?.let { GSON.fromJson(json) }
    @TypeConverter
    fun stateTypeToInt(type: InfoModel.StateData.Type): Int = type.value
    @TypeConverter
    fun intToStateType(value: Int): InfoModel.StateData.Type = InfoModel.StateData.Type.fromValue(value)
    @TypeConverter
    fun stateDayToInt(type: InfoModel.StateData.EmissionDay): Int = type.value
    @TypeConverter
    fun intToStateDay(value: Int): InfoModel.StateData.EmissionDay = InfoModel.StateData.EmissionDay.fromValue(value)
    @TypeConverter
    fun stringMapToJson(map: Map<String,String>?): String? = map?.toJson()
    @TypeConverter
    fun jsonToStringMap(json: String?): Map<String,String>? = json?.let { GSON.fromJson(it) }
}