package knf.hydra.module.test.retrofit

import knf.hydra.core.main.MainDbBridge
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.module.test.models.RelatedModel
import knf.hydra.module.test.models.TestDirectoryModel
import knf.hydra.module.test.models.TestRecentModel
import knf.hydra.module.test.models.TestSearchModel
import okhttp3.OkHttpClient
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit

object NetworkRepository {
    lateinit var currentBypass: BypassModel
    lateinit var currentDbBridge: MainDbBridge

    private val factory by lazy {
        Retrofit.Builder()
            .baseUrl("https://animeflv.net")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(JspoonConverterFactory.create())
            .build().create(NetworkFactory::class.java)
    }

    fun getRecents(bypassModel: BypassModel,bridge: MainDbBridge): List<TestRecentModel> {
        currentBypass = bypassModel
        currentDbBridge = bridge
        return factory.getRecents(bypassModel.asMap()).execute().body()?.list?.onEach {
            it.isChapterSeen = bridge.isChapterSeen(it.id,"test")
        }?: emptyList()
    }

    fun getInfo(url: String, bypassModel: BypassModel,bridge: MainDbBridge): InfoModel?{
        currentBypass = bypassModel
        currentDbBridge = bridge
        return try {
            factory.getInfo(url,bypassModel.asMap()).execute().body()
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    fun getRelatedInfo(url: String, bypassModel: BypassModel): RelatedModel? {
        currentBypass = bypassModel
        return try {
            factory.getRelatedInfo(url, bypassModel.asMap()).execute().body()
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    fun getDirectoryPage(page: Int, bypassModel: BypassModel,filters: FilterRequest?): List<TestDirectoryModel> {
        currentBypass = bypassModel
        val filtersMap = filters?.let { request ->
            val map = mutableMapOf<String,String>()
            request.filters.forEach { result ->
                result.selected.forEach { item ->
                    map[result.key] = item.value
                }
            }
            map
        }?: emptyMap()
        return factory.getDirectoryPage(page, bypassModel.asMap(), filtersMap).execute().body()!!.list
    }

    fun getCalendarPage(page: Int, bypassModel: BypassModel): List<TestDirectoryModel> {
        currentBypass = bypassModel
        return factory.getCalendarPage(page, bypassModel.asMap()).execute().body()?.list?: emptyList()
    }


    fun getSearchPage(query: String?, page: Int, bypassModel: BypassModel): List<TestSearchModel> {
        currentBypass = bypassModel
        return factory.getSearchPage(query?:"", page, bypassModel.asMap()).execute().body()!!.list
    }
}