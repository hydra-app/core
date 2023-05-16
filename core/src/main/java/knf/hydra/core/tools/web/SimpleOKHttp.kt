/*
 * Created by @UnbarredStream on 07/05/23 14:54
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 07/05/23 14:50
 */

package knf.hydra.core.tools.web

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

/**
 * Simplified okHttp request
 *
 * @param url Request url
 */
class SimpleOKHttp(url: String) {
    private var client = OkHttpClient()
    private val request = Request.Builder().url(url)

    /**
     * Set follow redirects
     *
     * @param redirect Follow redirect
     * @return This builder
     */
    fun followRedirects(redirect: Boolean) : SimpleOKHttp {
        client = client.newBuilder().followRedirects(redirect).build()
        return this
    }

    /**
     * Set the request User Agent
     *
     * @param ua Request User Agent
     * @return This builder
     */
    fun userAgent(ua: String): SimpleOKHttp {
        request.addHeader("User-Agent", ua)
        return this
    }

    /**
     * Add a header
     *
     * @param key Header key
     * @param value Header value
     * @return This builder
     */
    fun header(key: String, value: String): SimpleOKHttp {
        request.addHeader(key, value)
        return this
    }

    /**
     * Add a group of headers
     *
     * @param headers A map with the headers
     * @return This builder
     */
    fun headers(headers: Map<String,String>): SimpleOKHttp {
        headers.forEach {
            request.addHeader(it.key, it.value)
        }
        return this
    }

    /**
     * Use GET method
     *
     * @return Request response
     */
    suspend fun get(): Result<Response> {
        return withContext(Dispatchers.IO) {
            try {
                Result.OK(client.newCall(request.build()).execute())
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(e)
            }
        }
    }

    /**
     * Use POST method
     *
     * @param body Data of the request
     * @return Request response
     */
    suspend fun post(body: RequestBody): Result<Response> {
        return withContext(Dispatchers.IO) {
            try {
                Result.OK(client.newCall(request.post(body).build()).execute())
            } catch (e:Exception) {
                e.printStackTrace()
                Result.Error(e)
            }
        }
    }

    /**
     * Use GET method
     *
     * @return Request response as string
     */
    suspend fun getAsString(): Result<String> {
        return get().let {
            when(it) {
                is Result.OK -> Result.OK(it.data?.body?.string())
                is Result.Error -> Result.Error(it.error)
            }
        }
    }

    /**
     * Use POST method
     *
     * @param body Data of the request
     * @return Request response as string
     */
    suspend fun postAsString(body: RequestBody): Result<String> {
        return post(body).let {
            when(it) {
                is Result.OK -> Result.OK(it.data?.body?.string())
                is Result.Error -> Result.Error(it.error)
            }
        }
    }

    /**
     * Result base calss
     *
     * @param T Response data type
     * @property data Response data, null if error
     */
    sealed class Result<T>(val data: T?) {
        /**
         * Successful response
         */
        class OK<T>(data: T?): Result<T>(data)
        /**
         * Error response
         *
         * @param error Request throwable
         */
        class Error<T>(val error: Throwable): Result<T>(null)
    }

}