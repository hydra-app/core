/*
 * Created by @UnbarredStream on 08/04/22 19:10
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 19:06
 */

package knf.hydra.samples

/** @suppress */
class Samples {
    fun module() {
        class Module: HeadModule() {
            override val moduleVersionCode: Int = BuildConfig.VERSION_CODE
            override val moduleVersionName: String = BuildConfig.VERSION_NAME
            override val baseUrl: String = "https://empty.com"
            override val moduleName: String = "Test Module"
            override val dataRepository: HeadRepository = Repository()
            override val config: HeadConfig = TestConfig()
        }
    }
}