package knf.hydra.core.models

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Ignore
import knf.hydra.core.models.data.Category
import java.text.DecimalFormat

abstract class RecentModel{
    abstract var id: Int
    abstract var seriesId: Int
    abstract var name: String
    abstract var type: String
    abstract var chapter: Double
    abstract var episodeLink: String
    abstract var seriesLink: String
    abstract var category: Category
    open var seriesImage: String? = null
    open var chapterImage: String? = null
    @Ignore
    var isChapterSeen: Boolean = false
    @Ignore
    var isChapterDownloaded: Boolean = false
    @Ignore
    var isFavorite: Boolean? = null

    fun createName(): String{
        return if (type.contains("%s"))
            String.format(type, DecimalFormat("0.#").format(chapter))
        else
            type
    }

    class Notify(val module: String, val time: Long, val model: RecentModel){
        companion object{
            fun fromRecent(module: String, model: RecentModel): Notify = Notify(module, System.currentTimeMillis(), model)
        }
    }

    companion object{
        val DIFF = object : DiffUtil.ItemCallback<RecentModel>(){
            override fun areItemsTheSame(p0: RecentModel, p1: RecentModel): Boolean =
                p0.id == p1.id

            override fun areContentsTheSame(p0: RecentModel, p1: RecentModel): Boolean =
                p0.isChapterSeen == p1.isChapterSeen && p0.isChapterDownloaded == p1.isChapterDownloaded
        }
    }
}