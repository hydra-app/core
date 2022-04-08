/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models

import androidx.paging.PagingData
import knf.hydra.core.models.data.ExtraSection
import knf.hydra.core.models.data.VerticalImageItem
import kotlinx.coroutines.flow.Flow

/**
 * Represents a profile page
 */
abstract class ProfileModel {
    /** Profile name */
    abstract var name: String
    /** Profile info tabs */
    abstract var tabs: List<ProfileTab>
    /** Optional profile image */
    open var image: String? = null
    /** Optional header image background */
    open var background: String? = null
    /** Optional subtext */
    open var subText: String? = null
}

/**
 * Represents a profile page tab with data
 *
 * @property name Tab name
 */
sealed class ProfileTab(val name: String) {
    /** Info tab with several sections */
    class Info(name: String, val dataSections: List<ExtraSection>): ProfileTab(name)
    /** Directory tab with items */
    class DirectoryGrid(name: String, val pager: Flow<PagingData<DirectoryModel>>): ProfileTab(name)
    /** Gallery tab with multimedia */
    class GalleryGrid(name: String, val pager: Flow<PagingData<VerticalImageItem>>): ProfileTab(name)
}

