/*
 * Created by @UnbarredStream on 08/04/22 19:35
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 19:32
 */

package knf.hydra.module.test1.extras

import knf.hydra.core.HeadConfig

class TestConfig: HeadConfig() {
    init {
        isRecentsAvailable = true
        isCalendarEnabled = true
    }
}