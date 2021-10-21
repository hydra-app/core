package knf.hydra.core.models

import androidx.recyclerview.widget.DiffUtil
import knf.hydra.core.models.data.DownloadInfo
import java.text.DecimalFormat

abstract class ChapterModel {
    abstract var id: Int
    abstract var type: String
    abstract var number: Double
    abstract var chapterLink: String
    open var chapterTitle: String? = null
    open var seriesLink: String? = null
    open var thumbnailLink: String? = null
    open var commentsLink: String? = null
    var isChapterSeen: Boolean = false
    var chapterDownloadState: DownloadInfo.Light? = null

    fun createChapter(): String{
        return if (type.contains("%s"))
            String.format(type, DecimalFormat("0.#").format(number))
        else
            type
    }

    companion object{
        val DIFF = object : DiffUtil.ItemCallback<ChapterModel>(){
            override fun areItemsTheSame(p0: ChapterModel, p1: ChapterModel): Boolean =
                p0.id == p1.id

            override fun areContentsTheSame(p0: ChapterModel, p1: ChapterModel): Boolean =
                p0.isChapterSeen == p1.isChapterSeen && p0.chapterDownloadState == p1.chapterDownloadState
        }
    }
}