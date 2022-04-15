/*
 * Created by @UnbarredStream on 14/04/22 20:00
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 14/04/22 19:59
 */

package knf.hydra.core.models.data

import knf.hydra.core.models.data.DecodeResult.Failed
import knf.hydra.core.models.data.DecodeResult.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


/**
 * Base source data
 *
 * @see VideoSource
 * @see GallerySource
 * @see WebSource
 */
sealed class SourceData<T: SourceItem>(val itemsFlow: Flow<List<T>>)

/**
 * Base source item
 *
 * @see VideoItem
 * @see GalleryItem
 * @see WebItem
 */
sealed class SourceItem(val name: String, val link: String)

/**
 * Represents a video item in the source
 *
 * @param name Item name
 * @param link Item link
 * @param type Optional item subtext
 * @param quality Optional quality type (icon in the source list)
 * @param needDecoder Specify if this link needs to be processed before playing or it can be played instantly, default is **true**
 * @param canDownload Specify if this link can be downloaded
 * @param payload Optional payload for custom decoders
 */
class VideoItem(name: String, link: String, val type: String? = null, val quality: Quality? = null, val needDecoder: Boolean = true, val canDownload: Boolean = true, val payload: String? = null): SourceItem(name, link) {
    /**
     * Represents the quality of the video item
     */
    enum class Quality{ HIGH_4K, HIGH, MEDIUM, LOW, MULTIPLE }
}

/**
 * Represents an item in the gallery
 *
 * @param link The item link
 *
 * @property isVideo Specify if the link is a video, default is **false**
 * @property headers Optional map of headers needed to open the link
 */
class GalleryItem(link: String, val isVideo: Boolean = false, val headers: Map<String, String>? = null): SourceItem(name = "", link)

/**
 * Represents the data used to open the web page
 *
 * @param name Screen title
 * @param link Web link to be used
 */
class WebItem(name: String, link: String): SourceItem(name, link)

/**
 * Represents a source with videos
 *
 * @param items Video items
 */
class VideoSource(items: Flow<List<VideoItem>>): SourceData<VideoItem>(items)

/**
 * Represents a gallery with media
 *
 * @param items Gallery items
 */
class GallerySource(items: Flow<List<GalleryItem>>): SourceData<GalleryItem>(items)

/**
 * Represents a web page source
 *
 * @param name Screen title
 * @param link Web link to be used
 */
class WebSource(name: String, link: String): SourceData<WebItem>(flowOf(listOf(WebItem(name, link))))

/**
 * Represents a video decoder
 */
abstract class VideoDecoder {
    /**
     * Check if this decoder can process the [link]
     *
     * @param link Server link
     * @return True if the decoder can process the [link]
     */
    abstract fun canDecode(link: String): Boolean

    /**
     * Try to decode the [SourceItem]
     *
     * @param item The item to decode
     * @return The result of the operation, [DecodeResult.Success] or [DecodeResult.Failed]
     */
    abstract suspend fun decode(item: SourceItem): DecodeResult
}

/**
 * Base decode result
 *
 * @see Success
 * @see Failed
 */
sealed class DecodeResult(val list: List<Option>, val isSuccessful: Boolean = true){

    /**
     * Success result
     *
     * @constructor One or more [Option] for the source
     */
    class Success: DecodeResult {
        constructor(list: List<Option>): super(list, true)
        constructor(option: Option): this(listOf(option))
    }

    /**
     * Failed result
     */
    class Failed: DecodeResult(emptyList(), false)
}

/**
 * Option object containing the data for the decoded source
 *
 * @property directLink Direct link to the file (video file, no video hosting)
 * @property name Source option name
 * @property quality Optional display quality
 * @property headers Optional headers for this option
 */
class Option(val directLink: String, val name: String? = null, val quality: VideoItem.Quality? = null, val headers: Map<String,String>? = null)