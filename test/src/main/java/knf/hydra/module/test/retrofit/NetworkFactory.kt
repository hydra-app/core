package knf.hydra.module.test.retrofit

import knf.hydra.core.models.InfoModel
import knf.hydra.module.test.models.*
import retrofit2.Call
import retrofit2.http.*

interface NetworkFactory {
    @GET(".")
    fun getRecents(@HeaderMap bypass: Map<String,String>): Call<RecentsPage>

    @GET
    fun getInfo(@Url url: String, @HeaderMap bypass: Map<String,String>): Call<TestAnimeInfo>

    @GET
    fun getRelatedInfo(@Url url: String, @HeaderMap bypass: Map<String,String>): Call<RelatedModel>

    @GET("/browse")
    fun getDirectoryPage(@Query("page") page: Int, @HeaderMap bypass: Map<String,String>, @QueryMap filters: Map<String,String>): Call<DirectoryPage>

    @GET("/browse?status[]=1&order=title")
    fun getCalendarPage(@Query("page") page: Int, @HeaderMap bypass: Map<String,String>): Call<DirectoryPage>

    @GET("/browse")
    fun getSearchPage(@Query("q") query: String,@Query("page") page: Int, @HeaderMap bypass: Map<String,String>): Call<SearchPage>

}