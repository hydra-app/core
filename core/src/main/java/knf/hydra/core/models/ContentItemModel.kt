package knf.hydra.core.models

import androidx.recyclerview.widget.DiffUtil
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.data.DownloadInfo
import java.text.DecimalFormat

/**
 * Represents a content item
 */
abstract class ContentItemModel {
    /** Unique id for the item, for example the hash of the [itemLink] */
    abstract var id: Int
    /** Item type, you can include a %s so the app include the [number] (e.g. "Chapter %s" with number 5.2 would be shown in the app as Chapter 5.2) */
    abstract var type: String
    /** Item number, this will be used for sorting and sometimes for display purposes */
    abstract var number: Double
    /** Item link, this will be used to create the sources in [HeadRepository.sourceData] */
    abstract var itemLink: String
    /** Optional content title */
    open var itemTitle: String? = null
    /** Optional thumbnail image link */
    open var thumbnailLink: String? = null
    /** Optional comments web link */
    open var commentsLink: String? = null
    /** @suppress */
    var isItemSeen: Boolean = false
    /** @suppress */
    var itemDownloadState: DownloadInfo.Light? = null

    /** @suppress */
    fun createItemSubtitle(): String{
        return if (type.contains("%s"))
            String.format(type, DecimalFormat("0.#").format(number))
        else
            type
    }

    companion object{
        /** @suppress */
        val DIFF = object : DiffUtil.ItemCallback<ContentItemModel>(){
            override fun areItemsTheSame(p0: ContentItemModel, p1: ContentItemModel): Boolean =
                p0.id == p1.id

            override fun areContentsTheSame(p0: ContentItemModel, p1: ContentItemModel): Boolean =
                p0.isItemSeen == p1.isItemSeen && p0.itemDownloadState == p1.itemDownloadState
        }
    }
}