package knf.hydra.core.models.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Pattern
import kotlin.coroutines.coroutineContext

data class ExtraSection(val title: String, val dataFlow: Flow<ExtraData?>) {
    private val data: MutableStateFlow<ExtraData?> = MutableStateFlow(DummyData)
    private var isLoading = false

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

sealed class ExtraData

object DummyData : ExtraData()

sealed class ImageItem(val imageLink: String)

class VerticalImageItem(imageLink: String) : ImageItem(imageLink)

class HorizontalImageItem(imageLink: String) : ImageItem(imageLink)

class VideoItem(videoLink: String, val type: Type) : ImageItem(videoLink) {
    enum class Type {
        FILE, WEB
    }
}

class YoutubeItem(@Pattern("[\\w-]+") videoId: String) : ImageItem(videoId)

data class CollectionItem(
    val text: String,
    val subtext: String? = null,
    val image: ImageItem? = null,
    val clickAction: ClickAction? = null
)

data class ChipItem(
    val text: String,
    val image: String? = null,
    val clickAction: ClickAction? = null
)

data class Music(
    var title: String,
    var link: String,
    var subtitle: String? = null
)

sealed class ClickAction {
    class Info(val infoLink: String) : ClickAction()
    class Web(val link: String) : ClickAction()
    class DirectoryList(val title: String, val payload: String) : ClickAction()
    class Clipboard(val text: String) : ClickAction()
}

data class TextData(val text: String, val clickAction: ClickAction? = null) : ExtraData()

data class GalleryData(val list: List<ImageItem>) : ExtraData()

data class CollectionData(val list: List<CollectionItem>) : ExtraData()

data class ChipsData(val list: List<ChipItem>) : ExtraData()

data class YoutubeData(@Pattern("[\\w-]+") val videoId: String) : ExtraData()

data class MusicData(val list: List<Music>) : ExtraData()

fun ExtraData.asFlow(): Flow<ExtraData> = MutableStateFlow(this)