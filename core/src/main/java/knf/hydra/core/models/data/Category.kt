/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.models.data

/**
 * Categories for items.
 */
enum class Category(val value: Int){
    UNKNOWN(-1),
    ANIME(0),
    MOVIE(1),
    SERIES(2),
    HENTAI(3),
    PORN(4),
    TV(5),
    MANGA(6),
    BOOK(7);
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value }?: UNKNOWN
    }
}