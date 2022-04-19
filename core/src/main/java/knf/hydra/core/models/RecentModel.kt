/*
 * Created by @UnbarredStream on 18/04/22 19:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 18/04/22 18:28
 */

package knf.hydra.core.models

import knf.hydra.core.HeadRepository
import knf.hydra.core.models.data.Category
import knf.hydra.core.models.data.LayoutType
import java.text.DecimalFormat

/**
 * Represents a recent released item.
 *
 * It includes data about the content and the related info
 */
abstract class RecentModel {
    /** Unique content item ID */
    abstract var id: Int
    /** Unique id for the info item, for example the hash of the [infoLink] */
    abstract var infoId: Int
    /** Item name */
    abstract var name: String
    /** Item type, you can include a %s so the app include the [number] (e.g. "Chapter %s" with number 5.2 would be shown in the app as Chapter 5.2) */
    abstract var type: String
    /** Item number, this will be used for sorting and sometimes for display purposes */
    abstract var number: Double
    /** Item link, this will be used to create the sources in [HeadRepository.sourceData] */
    abstract var link: String
    /** Item info link */
    abstract var infoLink: String
    /** Content category */
    abstract var category: Category
    /**
     * Limit the interactions the user can use with this content.
     *
     * If the item is not media, the comments and import/export features will be disabled.
     */
    abstract var isMedia: Boolean
    /** Optional cover image for this item */
    open var infoImage: String? = null
    /** Optional thumbnail image for this item content */
    open var contentThumbnail: String? = null
    /**
     * Layout type to be used when loading the info, [LayoutType.SINGLE] for single items like [Category.MOVIE], or [LayoutType.MULTIPLE]
     * for multiple items like [Category.SERIES], by default the system uses the [category] to decide which one to use
     */
    open var infoLayoutType: LayoutType = if (category in listOf(Category.PORN, Category.MOVIE)) LayoutType.SINGLE else LayoutType.MULTIPLE

    /** @suppress */
    fun createSubtitle(): String {
        return if (type.contains("%s"))
            String.format(type, DecimalFormat("0.#").format(number))
        else
            type
    }

    /**
     * Represents a notified recent item
     *
     * @property module Module packaged registered for this item
     * @property time Last notified time
     * @property model Last recent model
     * @constructor Create empty Notify
     */
    class Notify(val module: String, val time: Long, val model: RecentModel) {
        companion object {
            /** @suppress */
            fun fromRecent(module: String, model: RecentModel): Notify = Notify(module, System.currentTimeMillis(), model)
        }
    }
}