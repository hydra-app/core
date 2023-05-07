/*
 * Created by @UnbarredStream on 07/05/23 14:54
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 07/05/23 14:50
 */

package knf.hydra.core.tools.web

import kotlinx.coroutines.CoroutineExceptionHandler
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
    private var errorCallback = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }

    /**
     * Set error callback
     *
     * @param callback Request error callback
     * @return This builder
     */
    fun setErrorCallback(callback: CoroutineExceptionHandler): SimpleOKHttp {
        errorCallback = callback
        return this
    }

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
    suspend fun get(): Response {
        return withContext(Dispatchers.IO + errorCallback) {
            client.newCall(request.build()).execute()
        }
    }

    /**
     * Use POST method
     *
     * @param body Data of the request
     * @return Request response
     */
    suspend fun post(body: RequestBody): Response {
        return withContext(Dispatchers.IO + errorCallback) {
            client.newCall(request.post(body).build()).execute()
        }
    }

    /**
     * Use GET method
     *
     * @return Request response as string
     */
    suspend fun getAsString(): String? {
        return get().body?.string()
    }

    /**
     * Use POST method
     *
     * @param body Data of the request
     * @return Request response as string
     */
    suspend fun postAsString(body: RequestBody): String? {
        return post(body).body?.string()
    }

}