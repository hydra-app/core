package knf.hydra.core.models

import androidx.paging.PagingData
import knf.hydra.core.models.data.ExtraSection
import knf.hydra.core.models.data.VerticalImageItem
import kotlinx.coroutines.flow.Flow

abstract class UserProfileModel {
    abstract var name: String
    abstract var tabs: List<ProfileTab>
    open var image: String? = null
    open var background: String? = null
    open var subText: String? = null
}

sealed class ProfileTab(val name: String) {
    class Info(name: String, val dataSections: List<ExtraSection>): ProfileTab(name)
    class DirectoryGrid(name: String, val pager: Flow<PagingData<DirectoryModel>>): ProfileTab(name)
    class GalleryGrid(name: String, val pager: Flow<PagingData<VerticalImageItem>>): ProfileTab(name)
}

