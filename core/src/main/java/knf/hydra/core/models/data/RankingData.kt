package knf.hydra.core.models.data

import androidx.annotation.FloatRange

data class RankingData(
    @FloatRange(from = 0.0, to = 5.0) val stars: Double,
    val count: Int? = null
)