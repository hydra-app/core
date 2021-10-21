package knf.hydra.core.models.data

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import knf.hydra.core.models.ChapterMin
import knf.hydra.core.models.InfoModelMin

@Entity
data class DownloadInfo(
    @PrimaryKey
    val id: Int,
    val module: String,
    val url: String,
    @Embedded(prefix = "info_")
    val info: InfoModelMin,
    @Embedded(prefix = "chapter_")
    val chapter: ChapterMin,
    val headers: Map<String, String>?,
    var progress: Int = 0,
    var state: Int = STATE_PENDING
) {
    data class Light(val id: Int, val progress: Int, val state: Int)
    companion object {
        const val STATE_PENDING = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_COMPLETED = 2

        val DIFF = object : DiffUtil.ItemCallback<DownloadInfo>(){
            override fun areItemsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
                return oldItem.progress == newItem.progress && oldItem.state == newItem.state
            }

            override fun getChangePayload(oldItem: DownloadInfo, newItem: DownloadInfo): Any = true
        }

        fun fromChapter(
            module: String,
            path: String,
            info: InfoModelMin,
            chapter: ChapterMin,
            link: String,
            headers: Map<String, String>?
        ): DownloadInfo {
            return DownloadInfo(
                "$module:${chapter.id}:$path".hashCode(),
                module,
                link,
                info,
                chapter,
                headers
            )
        }
    }
}