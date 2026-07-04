/*
 * Created by @UnbarredStream on 25/04/23 18:25
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 24/04/23 19:09
 */

package knf.hydra.core.tools.web

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Keep

/**
 * Convenience class to eval js on web pages
 *
 * @param context Context to create webview
 */
class WebJS(context: Context, wb: WebView? = null) {
    private val webView = wb ?: WebView(context)
    /** @suppress */
    val defaultUserAgent: String = webView.settings.userAgentString
    private var callback: ((String) -> Unit)? = null

    init {
        webView.settings.apply {
            javaScriptEnabled = true
        }
        webView.addJavascriptInterface(JSInterface{ callback?.invoke(it) },"myInterface")
    }

    /**
     * Eval de [js] code on the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param headers Optional additional headers
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @param callback Callback of eval code result
     */
    fun evalOnFinish(link: String, userAgent :String, headers: Map<String, String>,timeout: Long, js: String, callback: (String) -> Unit){
        this.callback = callback
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            webView.loadUrl("javascript:myInterface.returnResult(eval('try{$js}catch(e){e}'));")
            reset()
        }
        webView.settings.userAgentString = userAgent
        webView.settings.blockNetworkImage = true
        webView.webViewClient = object : DefaultClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request?.url?.toString()?.startsWith("http") != true) {
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable,timeout)
            }
        }
        webView.loadUrl(link, headers)
    }

    /**
     * Get the cookies of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param headers Optional additional headers
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @param cookies Callback of the cookies from the link
     */
    fun cookiesOnFinish(link: String, userAgent: String, headers: Map<String, String>, timeout: Long, cookies: (String) -> Unit) {
        val handler = Handler(Looper.getMainLooper())
        val callback = {
            cookies(CookieManager.getInstance().getCookie(link))
            reset()
        }
        webView.settings.userAgentString = userAgent
        webView.settings.blockNetworkImage = true
        webView.webViewClient = object : DefaultClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                handler.removeCallbacks(callback)
                handler.postDelayed(callback,timeout)
            }
        }
        webView.loadUrl(link, headers)
    }

    /**
     * Listen for network resources while loading a page and return the first one that matches the filter.
     *
     * This function loads the specified [link] in a WebView and intercepts network requests.
     * It uses the provided [filter] to identify a target resource. Once the page loading is
     * finished (or the [timeout] is reached), the [callback] is invoked with the URL and headers
     * of the first matched resource.
     *
     * @param link The URL to load in the WebView
     * @param userAgent The user agent string to use for the request
     * @param headers A map of additional headers to include in the main request.
     * @param timeout The maximum time in milliseconds to wait after the page has finished loading
     *                before invoking the callback. This ensures that any delayed or asynchronous
     *                resource requests are captured
     * @param executeOnFinish Optional JavaScript code to execute after the page has finished
     *                        loading (javascript:`<code>`)
     * @param filter A lambda function that takes a resource URL (as a `String?`) and returns `true`
     *               if it's the desired resource, `false` otherwise
     * @param callback A lambda function to be invoked with the URL and headers of the matched resource.
     *                 It receives the resource URL (`String?`) and its response headers (`Map<String, String>`).
     *                 If no resource matches the filter before the timeout, `null` and an empty map are returned.
     */
    fun listenResources(
        link: String,
        userAgent: String,
        headers: Map<String, String>,
        timeout: Long,
        executeOnFinish: String? = null,
        followRedirects: Boolean = false,
        filter: (String?, Map<String, String>?) -> Boolean,
        callback: (String?, Map<String, String>) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        var result: String? = null
        var headersResult: Map<String, String> = emptyMap()
        val runnable = kotlinx.coroutines.Runnable {
            callback(result, headersResult)
            reset()
        }
        webView.settings.userAgentString = userAgent
        webView.settings.blockNetworkImage = true
        webView.webViewClient = object : DefaultClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return followRedirects
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if (filter(request?.url?.toString(), request?.requestHeaders)) {
                    result = request?.url?.toString()
                    headersResult = request?.requestHeaders ?: emptyMap()
                    runnable.run()
                    handler.removeCallbacks(runnable)
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (executeOnFinish != null) {
                    webView.loadUrl(executeOnFinish)
                }
                super.onPageFinished(view, url)
            }
        }
        webView.loadUrl(link, headers)
        handler.postDelayed(runnable, timeout)
    }

    /**
     * Eval js on a webview
     *
     * @param code Code to eval
     * @param result Callback of the eval code result
     */
    fun evalJs(code: String, result: (String) -> Unit) {
        webView.evaluateJavascript(code, result)
    }

    private fun reset() {
        webView.webViewClient = object : DefaultClient() {}
        webView.loadUrl("about:blank")
    }

    /** @suppress */
    private abstract class DefaultClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean = false
    }

    /** @suppress */
    @Keep
    class JSInterface(private val callback: (String) -> Unit){
        /** @suppress */
        @JavascriptInterface
        fun returnResult(result: String){
            callback(result)
        }
    }
}