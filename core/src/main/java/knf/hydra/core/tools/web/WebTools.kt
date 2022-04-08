/*
 * Created by @UnbarredStream on 08/04/22 17:11
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 17:10
 */

package knf.hydra.core.tools.web

import android.content.Context
import androidx.annotation.RestrictTo
import de.prosiebensat1digital.oasisjsbridge.JsBridge
import de.prosiebensat1digital.oasisjsbridge.JsBridgeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Web tools used to get values and eval js on web pages
 */
object WebTools {
    private lateinit var webJs: WebJS
    private val packedRegex = "eval\\((function\\(p,a,c,k,e,?[dr]?\\).*.split\\('\\|'\\).*)\\)".toRegex()

    /** @suppress */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    fun init(context: Context){
        webJs = WebJS(context)
    }

    /** Search packed functions in the [link] html and decode ONE defined by [packedSelector], by default the first packed found will be decoded.
     *  Packed functions will be searched using this regex: eval\((function\(p,a,c,k,e,?[dr]?\).*.split\('\|'\).*)\)
     *
     *  @param link The link used to search the packed functions
     *  @param packedSelector Selects a single packed code from the list
     *  @return The decoded packed function selected by [packedSelector]
     */
    fun unpackLink(link: String, packedSelector: (values: List<String>) -> String = { it.first() }): String {
        val html = URL(link).readText()
        val packedCode = packedRegex.find(html)?.groups?.ifEmpty { return "error: No packed function found" }?.let { packedSelector(it.mapNotNull { it?.value }) }?: return "error: No packed function found"
        return unpack(packedCode)
    }

    /** Search packed functions in the [link] html and decode all.
     *  Packed functions will be searched using this regex: eval\((function\(p,a,c,k,e,?[dr]?\).*.split\('\|'\).*)\)
     *
     *  @param link The link used to search the packed functions
     *  @return The decoded packed functions found in the link
     */
    fun unpackLinkAll(link: String): List<String> {
        val html = URL(link).readText()
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
        val jsBridge = JsBridge(JsBridgeConfig.bareConfig())
        return jsBridge.evaluateBlocking("function prnt() {var txt = $packedCode; return txt;}prnt();")
    }

    /**
     * Decode multiple packed functions
     *
     * @param packedCodes The packed functions to be decoded
     * @return A list with the decoded functions in the same order as [packedCodes]
     */
    fun unpackAll(packedCodes: List<String>): List<String> {
        val jsBridge = JsBridge(JsBridgeConfig.bareConfig())
        return packedCodes.map { jsBridge.evaluateBlocking("function prnt() {var txt = $it; return txt;}prnt();") }
    }

    /**
     * Get the html of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param timeout Time to wait after onPageFinished is called before getting the html
     * @return The html of the link
     */
    suspend fun getHtml(link: String, userAgent: String = webJs.defaultUserAgent, timeout: Long = 1000): String? {
        return suspendCoroutine { continuation ->
            webJs.evalOnFinish(link, userAgent, timeout, "(\"<html>\"+document.getElementsByTagName(\"html\")[0].innerHTML+\"<\\/html>\")"){
                continuation.resume(it)
            }
        }
    }

    /**
     * Get the cookies of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @return The cookies of the link
     */
    suspend fun getCookies(link: String, userAgent: String = webJs.defaultUserAgent, timeout: Long = 1000): String? {
        return suspendCoroutine { continuation ->
            webJs.cookiesOnFinish(link, userAgent, timeout){
                continuation.resume(it)
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
                webJs.evalJs(code) {
                    continuation.resume(it)
                }
            }
        }
    }
}