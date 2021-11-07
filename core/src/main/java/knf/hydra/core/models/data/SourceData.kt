package knf.hydra.core.models.data

sealed class SourceData(val items: List<SourceItem>){
    enum class Type{ VIDEO, GALLERY }
}

/**
 * Source item
 *
 * @property name
 * @property link
 * @property type
 * @property quality
 * @property needDecoder Specify if this link needs to be processed before playing or it can be played instantly, default is **true**.
 * @property canDownload
 * @property payload
 * @constructor Create empty Source item
 */
data class SourceItem(val name: String, val link: String, val type: String? = null, val quality: Quality? = null, val needDecoder: Boolean = true, val canDownload: Boolean = true, val payload: String? = null){
    enum class Quality{ HIGH_4K, HIGH, MEDIUM, LOW, MULTIPLE }
}

class VideoSource(items: List<SourceItem>): SourceData(items)

class GallerySource(items: List<SourceItem>, val headers: List<Map<String,String>?>? = null): SourceData(items)

class WebSource(item: SourceItem): SourceData(listOf(item))

abstract class VideoDecoder {
    abstract fun canDecode(link: String): Boolean
    abstract suspend fun decode(item: SourceItem): DecodeResult
}

sealed class DecodeResult(val list: List<Option>, val isSuccessful: Boolean = true){
    class Success(list: List<Option>): DecodeResult(list, true)
    class Failed: DecodeResult(emptyList(), false)
}

class Option(val directLink: String, val name: String? = null, val quality: SourceItem.Quality? = null, val headers: Map<String,String>? = null)