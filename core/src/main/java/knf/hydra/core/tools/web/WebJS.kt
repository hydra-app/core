/*
 * Created by @UnbarredStream on 19/06/22 13:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 19/06/22 13:39
 */

package knf.hydra.core.tools.web

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.*
import androidx.annotation.Keep

/**
 * Convenience class to eval js on web pages
 *
 * @param context Context to create webview
 */
class WebJS(context: Context) {
    private val webView = WebView(context)
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
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @param callback Callback of eval code result
     */
    fun evalOnFinish(link: String, userAgent :String, timeout: Long, js: String, callback: (String) -> Unit){
        this.callback = callback
        val handler = Handler(Looper.getMainLooper())
        val runnable = {
            webView.loadUrl("javascript:myInterface.returnResult(eval('try{$js}catch(e){e}'));")
            reset()
        }
        webView.settings.userAgentString = userAgent
        webView.settings.blockNetworkImage = true
        webView.webViewClient = object : DefaultClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable,timeout)
            }
        }
        webView.loadUrl(link)
    }

    /**
     * Get the cookies of the [link] after being loaded in a Webview
     *
     * @param link Link to be loaded in the webview
     * @param userAgent Optional user agent to be used while loading the [link]
     * @param timeout Time to wait after onPageFinished is called before getting the cookies
     * @param cookies Callback of the cookies from the link
     */
    fun cookiesOnFinish(link: String, userAgent: String, timeout: Long, cookies: (String) -> Unit) {
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
        webView.loadUrl(link)
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