# Module Hydra core
## The Core library for Hydra modules
In order for the main app to detect and use the module it need to have a class named **Module** extending **HeadModule** in the root of the project

### Example
Inside the project root **(src/main/java|kotlin/&lt;package&#62;/&lt;here&#62;)** create a class named **Module** that extends [HeadModule](https://docs.knf-hydra.app/core/-hydra%20core/knf.hydra.core/-head-module).
- For more settings check [HeadConfig](https://docs.knf-hydra.app/core/-hydra%20core/knf.hydra.core/-head-config)
- For more information about the data repository check [HeadRepository](https://docs.knf-hydra.app/core/-hydra%20core/knf.hydra.core/-head-repository)

```kotlin
class Module: HeadModule() { // Needs to be named Module and extend HeadModule
    override val baseUrl: String = "https://example.com/"
    override val config: HeadConfig = object : HeadConfig(){ // Check HeadConfig for more settings
        init {
            isRecentsAvailable = true
            isDirectoryAvailable = true
            isSearchAvailable = true
            searchBarText = "Search here"
            customDecoders = listOf()
        }
    }
    override val dataRepository: HeadRepository = ExampleRepository() // Check HeadRepository
    override val moduleName: String = "Example module"
    override val moduleVersionCode: Int = BuildConfig.VERSION_CODE
    override val moduleVersionName: String = BuildConfig.VERSION_NAME
}
```


# Package knf.hydra.core
Essential classes for a Hydra Module

# Package knf.hydra.core.models
Classes representing data used to communicate the module with the main app.

**HeadModule**, **HeadConfig** and **HeadRepository** are here.

# Package knf.hydra.core.models.analytics
Class representing data used for the analytics feature

# Package knf.hydra.core.models.data
Classes representing extra data used in **core.models**

