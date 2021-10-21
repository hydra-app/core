package knf.hydra.core.models.analytics

import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.Category

data class InfoModelAnalytics(
    val id: Int,
    val name: String,
    val link: String,
    val category: Category,
    val genres: List<InfoModel.Tag>?,
    val tags: List<InfoModel.Tag>?
)