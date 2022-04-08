/*
 * Created by @UnbarredStream on 08/04/22 18:05
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 18:02
 */

package knf.hydra.core.models.data

import knf.hydra.core.HeadConfig
import knf.hydra.core.HeadRepository
import knf.hydra.core.models.data.ReviewConfig.State
import knf.hydra.core.models.data.ReviewConfig.State.*


/**
 * Data representing the review system configuration
 *
 * **At lest one state needs to be [State.REQUIRED]**
 *
 * @property starsState The state of the rating bar in the review dialog
 * @property commentaryState The state of the EditText in the review dialog
 * @property singleReview If enabled the main app will remember the submitted review and prevent the user for reviewing again
 * @property minRating **Optional:** Sets the minimum rating
 * @property commentaryMaxLength **Optional:** Sets the max length for the commentary, default to 150
 * @see HeadConfig.reviewConfig
 */
data class ReviewConfig(val starsState: State, val commentaryState: State, val singleReview: Boolean, val minRating: Double = 0.0, val commentaryMaxLength: Int = 150){
    /** State of review section, [DISABLED]/[OPTIONAL]/[REQUIRED] */
    enum class State{
        DISABLED, OPTIONAL, REQUIRED
    }

    /**
     * Check if the review configuration is valid
     *
     * @return true if at least one state is [State.REQUIRED]
     */
    fun isValid(): Boolean = starsState == State.REQUIRED || commentaryState == State.REQUIRED
}

/**
 * Data representing the result of an user review
 *
 * @property starts The user selected stars, null if [ReviewConfig.starsState] is [State.DISABLED] or the user didn't input a response when [State.OPTIONAL]
 * @property commentary The user commentary, null if [ReviewConfig.starsState] is [State.DISABLED] or the user didn't input a response when [State.OPTIONAL]
 * @see HeadRepository.sendReview
 */
data class ReviewResult(val starts: Double?, val commentary: String?)