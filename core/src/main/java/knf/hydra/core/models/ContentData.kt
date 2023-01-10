/*
 * Created by @UnbarredStream on 10/01/23 14:14
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 10/01/23 11:43
 */

package knf.hydra.core.models

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * Base content data.
 *
 * The info UI will change depending of the type of the data.
 */
sealed class ContentData {
    /**
     * Single model, used to show only one item, (e.g. a movie)
     *
     * @property model The single content item
     * @property canDownload Indicates if the content can be downloaded, default true
     */
    data class Single(val model: ContentItemModel, val canDownload: Boolean = true): ContentData()

    /**
     * Multiple model, used to show a list of items
     *
     * The list selector will only show when there is 2 or more elements on the list.
     *
     * @property name Items header, or "Chapters" if null
     * @property contentLists Flow for the content lists
     */
    data class Multiple(val name: String? = null, val contentLists: Flow<List<ContentList>>): ContentData()
}

/**
 * Content list
 *
 * @property name List name to show in the selector
 * @property paging A Paging flow containing the content items
 */
data class ContentList(
    val name: String,
    val paging: Flow<PagingData<ContentItemModel>>
)