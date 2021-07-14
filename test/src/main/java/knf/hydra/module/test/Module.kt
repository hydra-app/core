package knf.hydra.module.test

import knf.hydra.core.HeadConfig
import knf.hydra.core.HeadModule
import knf.hydra.core.HeadRepository
import knf.hydra.module.test.db.DB
import knf.hydra.module.test.extras.Repository
import knf.hydra.module.test.extras.TestConfig

class Module : HeadModule() {
    override val moduleVersionCode: Int = BuildConfig.VERSION_CODE
    override val moduleVersionName: String = BuildConfig.VERSION_NAME
    override val baseUrl: String get() = "https://animeflv.net"
    override val moduleName: String = "Test Module"
    override val iconRes: Int = R.drawable.test_icon_1
    override val dataRepository: HeadRepository = Repository()
    override val config: HeadConfig = TestConfig()
    override suspend fun onModuleInitialize() {
        DB.start(createRoomDatabase("db-test", DB::class).build())
    }
}