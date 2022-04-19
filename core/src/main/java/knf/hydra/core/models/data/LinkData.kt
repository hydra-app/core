/*
 * Created by @UnbarredStream on 18/04/22 19:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 18/04/22 13:47
 */

package knf.hydra.core.models.data

import androidx.core.util.PatternsCompat

/**
 * Represents an openable link, if the [link] is a valid URL and [isUserOpenable] is set to
 * true, the user can open the [link] in a web browser.
 *
 * @property link The link to be represented
 * @property isUserOpenable If enable the [link] can be opened in a web browser by the user
 */
data class LinkData(val link: String, val isUserOpenable: Boolean = true) {

    /** Checks if [link] is a valid url and [isUserOpenable] is set to true */
    fun isOpenable() = isUserOpenable && PatternsCompat.WEB_URL.toRegex().matches(link)
}