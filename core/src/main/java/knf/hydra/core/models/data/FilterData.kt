/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models.data

/**
 * Represents a filter section
 *
 * @property key The key that identifies the filter
 * @property name The name of the filter
 * @property filterType The selection type of the filter
 * @property items The list of options for the filter
 */
class FilterData(val key: String, val name: String, val filterType: Type, val items: List<FilterItem>){
    enum class Type{
        SINGLE,MULTIPLE
    }
}

/**
 * Represents an option in the filter
 *
 * @property value The value for this item
 * @property name The display name for this option
 * @property isDefault True if the options needs to be selected by default
 */
data class FilterItem(val value: String, val name:String, val isDefault :Boolean = false)

/**
 * Represents a filter request
 *
 * @property filters The filters that were changed since last applied filter
 */
class FilterRequest(val filters: List<FilterResult>)

/**
 * Represents a [FilterData] with the selected [FilterItem] (s)
 *
 * @property selected The items that were changed
 *
 * @param filterData The original data for the filter
 */
class FilterResult(filterData: FilterData, val selected: List<FilterItem>){
    val key: String = filterData.key
    val filterType: FilterData.Type = filterData.filterType
}