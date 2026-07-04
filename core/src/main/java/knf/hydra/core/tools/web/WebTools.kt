/*
 * Created by @UnbarredStream on 25/04/23 18:25
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 24/04/23 19:20
 */

package knf.hydra.core.tools.web

import android.content.Context
import android.webkit.WebView
import androidx.annotation.RestrictTo
import com.github.kittinunf.fuel.Fuel
import de.prosiebensat1digital.oasisjsbridge.JsBridge
import de.prosiebensat1digital.oasisjsbridge.JsBridgeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * A utility object that provides tools for web-related tasks,
 * such as evaluating JavaScript, decoding packed functions, and retrieving
 * HTML content or cookies from web pages.
 *
 * This object relies on an internal `WebView` to process web content.
 */
object WebTools {
    private lateinit var webJs: WebJS
    private lateinit var jsBridge: JsBridge
    private val packedRegex = "eval\\((function\\(p,a,c,k,e,?[dr]?\\).*.split\\('\\|'\\).*)\\)".toRegex()

    /** @suppress */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    fun init(context: Context, webView: WebView? = null){
        webJs = WebJS(context, webView)
        jsBridge = JsBridge(JsBridgeConfig.bareConfig(), context)
    }

    /** Search packed functions in the [link] html and decode ONE defined by [packedSelector], by default the first packed found will be decoded.
     *  Packed functions will be searched using this regex: eval\((function\(p,a,c,k,e,?\&#91;dr&#93;?\).*.split\('\|'\).*)\)
     *
     *  @param link The link used to search the packed functions
     *  @param headers Optional additional headers
     *  @param packedSelector Selects a single packed code from the list
     *  @return The decoded packed function selected by [packedSelector]
     */
    fun unpackLink(link: String, headers: Map<String, String> = emptyMap(), packedSelector: (values: List<String>) -> String = { it.first() }): String {
        val html = Fuel.get(link).header(headers).responseString().third.get()
        val packedCode = packedRegex.find(html)?.groups?.ifEmpty { return "error: No packed function found" }?.let { packedSelector(it.mapNotNull { it?.value }) }?: return "error: No packed function found"
        return unpack(packedCode)
    }

    /** Search packed functions in the [link] html and decode all.
     *  Packed functions will be searched using this regex: eval\((function\(p,a,c,k,e,?\&#91;dr&#93;?\).*.split\('\|'\).*)\)
     *
     *  @param link The link used to search the packed functions
     *  @param headers Optional additional headers
     *  @return The decoded packed functions found in the link
     */
    fun unpackLinkAll(link: String, headers: Map<String, String> = emptyMap()): List<String> {
        val html = Fuel.get(link).header(headers).responseString().third.get()
        val packedCodes = packedRegex.find(html)?.groups?.mapNotNull { it?.value }?.ifEmpty { null }?: return emptyList()
        return unpackAll(packedCodes)
    }

    /**
     * Decode a single packed function
     *
     * @param packedCode The packed function to be decoded
     * @return The decoded packed function
     */
    fun unpack(packedCode: String): String {
        return jsBridge.evaluateBlocking("function prnt() {var txt = $packedCode; return txt;}prnt();")
    }

    /**
     * Decode multiple packed functions
     *
     * @param packedCodes The packed functions to be decoded
     * @return A list with the decoded functions in the same order as [packedCodes]
     */
    fun unpackAll(packedCodes: List<String>): List<String> {
        return packedCodes.map { jsBridge.evaluateBlocking("function prnt() {var txt = $it; return txt;}prnt();") }
    }


    /**
     * Eval a js code after loading a [link]
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param headers Optional additional headers
     * @param timeout Time to wait after onPageFinished is called before evaluating the code
     * @param code The code to be evaluated
     * @return The result of the eval code
     */
    suspend fun evalOnFinish(link: String, js: String, userAgent: String = webJs.defaultUserAgent, headers: Map<String, String> = emptyMap(), timeout: Long = 1000): String? {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                var isResponded = false
                webJs.evalOnFinish(link, userAgent, headers, timeout, js){
                    if (!isResponded){
                        isResponded = true
                        continuation.resume(it)
                    }
                }
            }
        }
    }

    /**
     * Get the html of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param headers Optional additional headers
     * @param timeout Time to wait after onPageFinished is called before getting the html
     * @return The html of the link
     */
    suspend fun getHtml(link: String, userAgent: String = webJs.defaultUserAgent, headers: Map<String, String> = emptyMap(), timeout: Long = 1000): String? {
        return evalOnFinish(link, "(\"<html>\"+document.getElementsByTagName(\"html\")[0].innerHTML+\"<\\/html>\")", userAgent, headers, timeout)
    }

    /**
     * Get the cookies of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param headers Optional additional headers
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @return The cookies of the link
     */
    suspend fun getCookies(link: String, userAgent: String = webJs.defaultUserAgent, headers: Map<String, String> = emptyMap(), timeout: Long = 1000): String? {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                var isResponded = false
                webJs.cookiesOnFinish(link, userAgent, headers, timeout){
                    if (!isResponded){
                        isResponded = true
                        continuation.resume(it)
                    }
                }
            }
        }
    }

    /**
     * Eval js on a webview
     *
     * @param code Code to eval
     * @return The result of the eval code
     */
    suspend fun evalJS(code: String): String {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                var isResponded = false
                webJs.evalJs(code) {
                    if (!isResponded){
                        isResponded = true
                        continuation.resume(it)
                    }
                }
            }
        }
    }

    suspend fun listenResources(
        link: String,
        filter: (String?, Map<String, String>?) -> Boolean,
        executeOnFinish: String? = null,
        userAgent: String = webJs.defaultUserAgent,
        headers: Map<String, String> = emptyMap(),
        followRedirects: Boolean = false,
        timeout: Long = 5000
    ): Pair<String?, Map<String, String>> {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                var isResponded = false
                webJs.listenResources(link, userAgent, headers, timeout, executeOnFinish, followRedirects, filter) { url, headers ->
                    if (!isResponded){
                        isResponded = true
                        continuation.resume(url to headers)
                    }
                }
            }
        }
    }
}