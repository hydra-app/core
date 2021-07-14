package knf.hydra.core.models

import android.os.Parcelable
import knf.hydra.core.models.data.Category
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoModelMin(
    var id: Int = 0,
    var name: String = "",
    var link: String = "",
    var category: Category = Category.UNKNOWN,
    var coverImage: String? = null
): Parcelable