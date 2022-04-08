/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models.data

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import knf.hydra.core.models.ContentItemMin
import knf.hydra.core.models.InfoModelMin

/** @suppress */
@Entity
data class DownloadInfo(
    @PrimaryKey
    val id: Int,
    val module: String,
    val url: String,
    @Embedded(prefix = "info_")
    val info: InfoModelMin,
    @Embedded(prefix = "content_")
    val contentItem: ContentItemMin,
    val headers: Map<String, String>?,
    var progress: Int = 0,
    var state: Int = STATE_PENDING
) {
    /** @suppress */
    data class Light(val id: Int, val progress: Int, val state: Int)
    companion object {
        /** @suppress */
        const val STATE_PENDING = 0
        /** @suppress */
        const val STATE_DOWNLOADING = 1
        /** @suppress */
        const val STATE_COMPLETED = 2
        /** @suppress */
        val DIFF = object : DiffUtil.ItemCallback<DownloadInfo>(){
            override fun areItemsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
                return oldItem.progress == newItem.progress && oldItem.state == newItem.state
            }

            override fun getChangePayload(oldItem: DownloadInfo, newItem: DownloadInfo): Any = true
        }

        /** @suppress */
        fun fromContent(
            module: String,
            path: String,
            info: InfoModelMin,
            contentItem: ContentItemMin,
            link: String,
            headers: Map<String, String>?
        ): DownloadInfo {
            return DownloadInfo(
                "$module:${contentItem.id}:$path".hashCode(),
                module,
                link,
                info,
                contentItem,
                headers
            )
        }
    }
}