package knf.hydra.module.test1

import knf.hydra.core.HeadConfig
import knf.hydra.core.HeadModule
import knf.hydra.core.HeadRepository
import knf.hydra.module.test1.extras.Repository
import knf.hydra.module.test1.extras.TestConfig

class Module: HeadModule() {
    override val moduleVersionCode: Int = BuildConfig.VERSION_CODE
    override val moduleVersionName: String = BuildConfig.VERSION_NAME
    override val baseUrl: String = "https://empty.com"
    override val moduleName: String = "Test Module"
    override val dataRepository: HeadRepository = Repository()
    override val config: HeadConfig = TestConfig()
}