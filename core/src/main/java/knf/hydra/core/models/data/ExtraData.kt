package knf.hydra.core.models.data

import org.intellij.lang.annotations.Pattern

data class ExtraSection(val title: String, val data: ExtraData)

sealed class ExtraData

sealed class ImageItem(val imageLink: String)

class VerticalImageItem(imageLink: String): ImageItem(imageLink)

class HorizontalImageItem(imageLink: String): ImageItem(imageLink)

class VideoItem(videoLink: String, val type: Type): ImageItem(videoLink){
    enum class Type{
        FILE, WEB
    }
}

class YoutubeItem(@Pattern("[\\w-]+") videoId: String): ImageItem(videoId)

data class CollectionItem(val text: String, val subtext: String? = null, val image: ImageItem? = null, val clickAction: ClickAction? = null)

data class ChipItem(val text: String, val image: String? = null, val clickAction: ClickAction? = null)

data class Music(
    var title: String,
    var link: String,
    var subtitle: String? = null
)

sealed class ClickAction{
    class Info(val infoLink: String): ClickAction()
    class Web(val link: String): ClickAction()
    class DirectoryList(val title: String, val payload: String): ClickAction()
    class Clipboard(val text: String): ClickAction()
}

data class TextData(val text: String, val clickAction: ClickAction? = null): ExtraData()

data class GalleryData(val list: List<ImageItem>): ExtraData()

data class CollectionData(val list: List<CollectionItem>): ExtraData()

data class ChipsData(val list: List<ChipItem>): ExtraData()

data class YoutubeData(@Pattern("[\\w-]+") val videoId: String): ExtraData()

data class MusicData(val list: List<Music>): ExtraData()