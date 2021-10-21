package knf.hydra.core.tools.web

import android.content.Context
import de.prosiebensat1digital.oasisjsbridge.JsBridge
import de.prosiebensat1digital.oasisjsbridge.JsBridgeConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object WebTools {
    private lateinit var webJs: WebJS
    private val packedRegex = "eval\\((function\\(p,a,c,k,e,?[dr]?\\).*.split\\('\\|'\\).*)\\)".toRegex()

    fun init(context: Context){
        webJs = WebJS(context)
    }

    fun unpack(link: String): String {
        val html = URL(link).readText()
        val jsBridge = JsBridge(JsBridgeConfig.bareConfig())
        val packedCode = packedRegex.find(html)?.destructured?.component1()
        return jsBridge.evaluateBlocking("function prnt() {var txt = $packedCode; return txt;}prnt();")
    }

    suspend fun getHtml(link: String, userAgent: String = webJs.defaultUserAgent, timeout: Long = 1000): String? {
        return suspendCoroutine { continuation ->
            webJs.evalOnFinish(link, userAgent, timeout, "(\"<html>\"+document.getElementsByTagName(\"html\")[0].innerHTML+\"<\\/html>\")"){
                continuation.resume(it)
            }
        }
    }

    suspend fun getCookies(link: String, userAgent: String = webJs.defaultUserAgent, timeout: Long = 1000): String? {
        return suspendCoroutine { continuation ->
            webJs.cookiesOnFinish(link, userAgent, timeout){
                continuation.resume(it)
            }
        }
    }

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