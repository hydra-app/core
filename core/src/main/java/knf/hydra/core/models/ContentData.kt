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
     */
    data class Single(val model: ContentItemModel): ContentData()

    /**
     * Multiple model, used to show a list of items
     *
     * @property contentPaging Flow paging for the content items
     */
    data class Multiple(val contentPaging: Flow<PagingData<ContentItemModel>>): ContentData()
}