package knf.hydra.core.models.data

import knf.hydra.core.HeadRepository
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.ClickAction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Pattern
import kotlin.coroutines.coroutineContext

/**
 * Represents a section with data
 *
 * @property title Section title
 * @property dataFlow A [flow](https://developer.android.com/kotlin/flow#create) containing the data for the section.
 */
data class ExtraSection(val title: String, val dataFlow: Flow<ExtraData?>) {
    private val data: MutableStateFlow<ExtraData?> = MutableStateFlow(DummyData)
    private var isLoading = false
    /** @suppress */
    suspend fun retrieveData(): Flow<ExtraData?> {
        if (data.value is DummyData && !isLoading) {
            isLoading = true
            CoroutineScope(coroutineContext).launch {
                data.value =
                    try {
                        dataFlow.catch { it.printStackTrace() }.first()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
            }
        }
        return data
    }
}

/**
 * Base class for section data.
 */
sealed class ExtraData

/** @suppress */
object DummyData : ExtraData()

/**
 * Base media item.
 *
 * @see ImageMediaItem
 * @see VideoMediaItem
 * @see YoutubeItem
 */
sealed class MediaItem(val imageLink: String)

/**
 * Base image media item
 *
 * @see VerticalImageItem
 * @see HorizontalImageItem
 */
sealed class ImageMediaItem(imageLink: String): MediaItem(imageLink)

/**
 * Represents a vertical image item.
 *
 * @param imageLink Image URL
 */
class VerticalImageItem(imageLink: String) : ImageMediaItem(imageLink)

/**
 * Represents an horizontal image item.
 *
 * @param imageLink Image URL
 */
class HorizontalImageItem(imageLink: String) : ImageMediaItem(imageLink)

/**
 * Represents a video item.
 *
 * @param videoLink Video URL
 * @property usePlayer If true the app will try to stream the video using the built-in player (requires a direct link to the video file), otherwise it will played in a web browser
 */
class VideoMediaItem(videoLink: String, val usePlayer: Boolean) : MediaItem(videoLink)

/**
 * Youtube media item for the built-in youtube player.
 *
 * @param videoId Youtube video ID (e.g. youtube.com/watch?v=**>video id<**)
 */
class YoutubeItem(@Pattern("[\\w-]+") videoId: String) : MediaItem(videoId)

/**
 * Represents an item in the collection.
 *
 * @property text Media text
 * @property subtext Media subtext
 * @property media [Vertical][VerticalImageItem] or [Horizontal][HorizontalImageItem] image item
 * @property clickAction Click behaviour
 */
data class CollectionItem(
    val text: String,
    val subtext: String? = null,
    val media: ImageMediaItem? = null,
    val clickAction: ClickAction? = null
)

/**
 * Represents a chip in the list.
 *
 * @property text Chip text
 * @property image Chip image
 * @property clickAction Click behaviour for the chip
 */
data class ChipItem(
    val text: String,
    val image: String? = null,
    val clickAction: ClickAction? = null
)

/**
 * Represents a music track.
 *
 * @property title Track title.
 * @property link Track file URL, see [this](https://developer.android.com/guide/topics/media/media-formats#audio-formats) for supported audio formats.
 * @property subtitle Track subtitle.
 */
data class Music(
    var title: String,
    var link: String,
    var subtitle: String? = null
)

/**
 * Click behaviour
 *
 * @see Info
 * @see Web
 * @see ExtraDirectory
 * @see Clipboard
 */
sealed class ClickAction {
    /**
     * Opens a new information screen.
     *
     * @property infoLink Info link used to open the [Info page][HeadRepository.infoPage]
     */
    class Info(val infoLink: String) : ClickAction()

    /**
     * Opens a web screen.
     *
     * @property link Page url
     */
    class Web(val link: String) : ClickAction()

    /**
     * Opens a directory screen using the specified payload.
     *
     * @property title Screen title
     * @property payload Payload to be used in the [repository][HeadRepository.extraDirectoryPager]
     */
    class ExtraDirectory(val title: String, val payload: String) : ClickAction()

    /**
     * Opens a profile screen.
     *
     * @property data Profile data to be used
     */
    class Profile(val data: InfoModel.ProfileData): ClickAction()

    /**
     * Copy the specified text into the clipboard on long click.
     *
     * @property text The text to be copied
     */
    class Clipboard(val text: String) : ClickAction()
}

/**
 * Represents a text extra.
 *
 * @property text Extra text
 * @property clickAction Click behaviour
 */
data class TextData(val text: String, val clickAction: ClickAction? = null) : ExtraData()

/**
 * Represents a gallery of images and/or videos without titles using default click behaviours:
 *
 * - Images: Zoomed with the option to save the image.
 * - Video: Web browser or built-in player depending of the link.
 * - Youtube: Open the video in the Youtube app or the browser it it's not installed.
 *
 * @property list The list of media items
 */
data class GalleryData(val list: List<MediaItem>) : ExtraData()

/**
 * Represents a collection of items with a title, optional image and an optional [ClickAction].
 *
 * @property list The list of collection items
 */
data class CollectionData(val list: List<CollectionItem>) : ExtraData()

/**
 * Represents a list of [chips](https://m3.material.io/components/chips/overview).
 *
 * @property list The list of chips
 */
data class ChipsData(val list: List<ChipItem>) : ExtraData()

/**
 * Represents a youtube video.
 *
 * @property videoId Id of the youtube video, usually the last part of the link
 */
data class YoutubeData(@Pattern("[\\w-]+") val videoId: String) : ExtraData()

/**
 * Represents a list of music tracks.
 *
 * @property list The list of tracks
 */
data class MusicData(val list: List<Music>) : ExtraData()

/**
 * Convenience extension to create a flow based in a data object.
 *
 * @return A state flow containing the data
 */
fun ExtraData.asFlow(): Flow<ExtraData> = MutableStateFlow(this)