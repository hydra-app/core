package knf.hydra.core.models

import androidx.recyclerview.widget.DiffUtil
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.LayoutType
import knf.hydra.core.models.data.RankingData

abstract class DirectoryModel {
    abstract var id: Int
    abstract var name: String
    abstract var infoLink: String
    abstract var category: Category
    open var infoLayoutType: LayoutType = if (category in listOf(Category.PORN, Category.MOVIE)) LayoutType.SINGLE else LayoutType.MULTIPLE
    open var orientation: Orientation = Orientation.VERTICAL
    open var type: String? = null
    open var imageLink: String? = null
    open var rankingData: RankingData? = null

    enum class Orientation { VERTICAL, HORIZONTAL }

    companion object{
        val DIFF = object : DiffUtil.ItemCallback<DirectoryModel>(){
            override fun areItemsTheSame(p0: DirectoryModel, p1: DirectoryModel): Boolean =
                p0.id == p1.id

            override fun areContentsTheSame(p0: DirectoryModel, p1: DirectoryModel): Boolean =
                p0.rankingData?.stars == p1.rankingData?.stars
        }
    }
}