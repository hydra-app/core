package knf.hydra.module.test.retrofit

import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.InfoModel
import knf.hydra.core.models.data.FilterRequest
import knf.hydra.core.models.data.ReviewResult
import knf.hydra.module.test.models.RelatedModel
import knf.hydra.module.test.models.TestDirectoryModel
import knf.hydra.module.test.models.TestRecentModel
import knf.hydra.module.test.models.TestSearchModel
import okhttp3.OkHttpClient
import org.json.JSONObject
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetworkRepository {
    val defaultCookies = mapOf("device" to "Computer")
    lateinit var currentBypass: BypassModel

    private val factory by lazy {
        Retrofit.Builder()
            .baseUrl("https://animeflv.net")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(JspoonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build().create(NetworkFactory::class.java)
    }

    fun getRecents(bypassModel: BypassModel): List<TestRecentModel> {
        currentBypass = bypassModel
        return factory.getRecents(bypassModel.asMap(defaultCookies)).execute().body()?.list?.filter { it.infoId != -1 }?: emptyList()
    }

    fun getInfo(url: String, bypassModel: BypassModel): InfoModel?{
        currentBypass = bypassModel
        return try {
            factory.getInfo(url,bypassModel.asMap(defaultCookies)).execute().body()
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    fun getRelatedInfo(url: String, bypassModel: BypassModel): RelatedModel? {
        currentBypass = bypassModel
        return try {
            factory.getRelatedInfo(url, bypassModel.asMap(defaultCookies)).execute().body()
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
        return factory.getDirectoryPage(page, bypassModel.asMap(defaultCookies), filtersMap).execute().body()!!.list
    }

    fun getCalendarPage(page: Int, bypassModel: BypassModel): List<TestDirectoryModel> {
        currentBypass = bypassModel
        return factory.getCalendarPage(page, bypassModel.asMap(defaultCookies)).execute().body()?.list?: emptyList()
    }


    fun getSearchPage(query: String?, page: Int, bypassModel: BypassModel): List<TestSearchModel> {
        currentBypass = bypassModel
        return factory.getSearchPage(query?:"", page, bypassModel.asMap(defaultCookies)).execute().body()!!.list
    }

    fun sendReview(id: Int, review: ReviewResult, bypassModel: BypassModel): Boolean {
        return try {
            JSONObject(factory.postReview((review.starts?:1.0).toInt(), id, bypassModel.asMap(defaultCookies)).execute().body()!!).getInt("success") == 1
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
}