package knf.hydra.core.models.data

import android.os.Parcelable
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.InfoModel
import kotlinx.parcelize.Parcelize

/**
 * Extra directory request used to load [HeadRepository.extraDirectoryPager]
 *
 * @property title The title of the screen
 * @property payload The payload used to load the directory
 */
@Parcelize
data class ExtraDirectoryRequest(val title: String, val payload: String?): Parcelable{
    companion object {
        fun from(tag: InfoModel.Tag) = ExtraDirectoryRequest(tag.name, tag.payload)
    }
}