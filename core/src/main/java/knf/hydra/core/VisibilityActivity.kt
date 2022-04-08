/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core

import android.app.Activity
import android.os.Bundle
/** @suppress */
internal class VisibilityActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}