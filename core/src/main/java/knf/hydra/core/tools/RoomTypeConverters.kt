/*
 * Created by @UnbarredStream on 08/04/22 18:05
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:58
 */

package knf.hydra.core.tools

import androidx.room.TypeConverter
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.ExtraData
import knf.tools.gsonktx.GSON
import knf.tools.gsonktx.toJson

/** @suppress */
class RoomTypeConverters {
    /** @suppress */
    @TypeConverter
    fun stringListToString(list: List<String>?): String? = list?.joinToString { ":,:" }
    /** @suppress */
    @TypeConverter
    fun stringToStringList(text: String?): List<String>? = text?.split(":,:")
    /** @suppress */
    @TypeConverter
    fun relatedListToJson(list: List<InfoModel.Related>?): String? = list?.toJson()
    /** @suppress */
    @TypeConverter
    fun jsonToRelatedList(json:String?): List<InfoModel.Related>? = json?.let { GSON.fromJson(json) }
    /** @suppress */
    @TypeConverter
    fun extraDataToJson(data:ExtraData?): String? = data?.toJson()
    /** @suppress */
    @TypeConverter
    fun jsonToExtraData(json:String?): ExtraData? = json?.let { GSON.fromJson(json) }
    /** @suppress */
    @TypeConverter
    fun stateTypeToInt(type: InfoModel.StateData.Type): Int = type.value
    /** @suppress */
    @TypeConverter
    fun intToStateType(value: Int): InfoModel.StateData.Type = InfoModel.StateData.Type.fromValue(value)
    /** @suppress */
    @TypeConverter
    fun stateDayToInt(type: InfoModel.StateData.EmissionDay): Int = type.value
    /** @suppress */
    @TypeConverter
    fun intToStateDay(value: Int): InfoModel.StateData.EmissionDay = InfoModel.StateData.EmissionDay.fromValue(value)
    /** @suppress */
    @TypeConverter
    fun stringMapToJson(map: Map<String,String>?): String? = map?.toJson()
    /** @suppress */
    @TypeConverter
    fun jsonToStringMap(json: String?): Map<String,String>? = json?.let { GSON.fromJson(it) }
}