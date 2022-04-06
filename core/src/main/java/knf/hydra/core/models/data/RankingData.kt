package knf.hydra.core.models.data

import androidx.annotation.FloatRange

/**
 * Represents a ranking data
 *
 * @property stars The star rating (0-5)
 * @property count Rating count
 */
data class RankingData(
    @FloatRange(from = 0.0, to = 5.0) val stars: Double,
    val count: Int? = null
)