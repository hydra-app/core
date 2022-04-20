/*
 * Created by @UnbarredStream on 20/04/22 13:10
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 20/04/22 12:50
 */

package knf.hydra.core.models.data

/**
 * Represents a filter section, each one contains a list of [options][FilterItem], the behavior is controlled by the [filterType]
 * which change the options between a list of checkboxes and radio buttons, when the filters are applied by the user the app will
 * detect the differences between the filters and will return only the changed sections.
 *
 * @property key The key that identifies the filter
 * @property name The name of the filter
 * @property filterType The selection type of the filter
 * @property items The list of options for the filter
 */
class FilterData(val key: String, val name: String, val filterType: Type, val items: List<FilterItem>){
    /**
     * Filter selection type, [SINGLE]/[MULTIPLE]
     */
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