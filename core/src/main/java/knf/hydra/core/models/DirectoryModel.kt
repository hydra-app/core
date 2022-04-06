package knf.hydra.core.models

import androidx.recyclerview.widget.DiffUtil
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.DirectoryModel.Orientation.HORIZONTAL
import knf.hydra.core.models.DirectoryModel.Orientation.VERTICAL
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.LayoutType
import knf.hydra.core.models.data.RankingData

/**
 * Represents an item in the directory
 */
abstract class DirectoryModel {
    /** Unique id for the item, for example the hash of the [infoLink] */
    abstract var id: Int
    /** Item name */
    abstract var name: String
    /** Info link used to open the [Info page][HeadRepository.infoPage] */
    abstract var infoLink: String
    /** Content category */
    abstract var category: Category
    /** Layout type to be used when loading the info, [LayoutType.SINGLE] for single items like [Category.MOVIE], or [LayoutType.MULTIPLE] for multiple items like [Category.SERIES], by default the
     * system uses the [category] to decide wich one to use */
    open var infoLayoutType: LayoutType = if (category in listOf(Category.PORN, Category.MOVIE)) LayoutType.SINGLE else LayoutType.MULTIPLE
    /** Image orientation to be used */
    open var orientation: Orientation = VERTICAL
    /** Optional subtext to be shown */
    open var type: String? = null
    /** Item image link */
    open var imageLink: String? = null
    /** Optional ranking data */
    open var rankingData: RankingData? = null

    /**
     * Represents the orientation on the image, [VERTICAL] and [HORIZONTAL]
     */
    enum class Orientation { VERTICAL, HORIZONTAL }

    companion object{
        /** @suppress */
        val DIFF = object : DiffUtil.ItemCallback<DirectoryModel>(){
            override fun areItemsTheSame(p0: DirectoryModel, p1: DirectoryModel): Boolean =
                p0.id == p1.id

            override fun areContentsTheSame(p0: DirectoryModel, p1: DirectoryModel): Boolean =
                p0.rankingData?.stars == p1.rankingData?.stars
        }
    }
}