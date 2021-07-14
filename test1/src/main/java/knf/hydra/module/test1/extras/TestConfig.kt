package knf.hydra.module.test1.extras

import knf.hydra.core.HeadConfig

class TestConfig: HeadConfig() {
    init {
        isRecentsAvailable = true
    }
}