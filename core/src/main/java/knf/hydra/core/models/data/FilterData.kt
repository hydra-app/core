package knf.hydra.core.models.data

class FilterData(val key: String, val name: String, val filterType: Type, val items: List<FilterItem>){
    enum class Type{
        SINGLE,MULTIPLE
    }
}

data class FilterItem(val value: String, val name:String, val isDefault :Boolean = false)

class FilterRequest(val filters: List<FilterResult>)

class FilterResult(filterData: FilterData, val selected: List<FilterItem>){
    val key: String = filterData.key
    val filterType: FilterData.Type = filterData.filterType
}