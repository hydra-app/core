/*
 * Created by @UnbarredStream on 25/04/23 18:25
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 24/04/23 20:53
 */

package knf.hydra.core.tools.web

import android.content.Context
import com.github.kittinunf.fuel.Fuel
import de.prosiebensat1digital.oasisjsbridge.JsBridge
import de.prosiebensat1digital.oasisjsbridge.JsBridgeConfig

/**
 * Tool to decode [Packer](http://dean.edwards.name/packer/) code in JS
 */
object Unpacker {
    private val packedRegex1 =
        "(function.*\\}\\s*\\('.*',\\s*.*?,\\s*\\d+,\\s*'.*?'\\.split\\('\\|'\\),\\d+,\\{.*\\}\\))".toRegex()
    private val packedRegex2 =
        "eval\\((function\\(p,a,c,k,e,?[dr]?\\).*.split\\('\\|'\\).*)\\)".toRegex()

    /**
     * Search packed code and decode it
     *
     * @param context Context
     * @param link Link to search for packed
     * @return The decoded code
     */
    fun unpack(context: Context, link: String): String {
        val html = Fuel.get(link).responseString().third.get()
        val jsBridge = JsBridge(JsBridgeConfig.bareConfig(), context)
        val packedCode = packedRegex2.find(html)?.destructured?.component1()
        return jsBridge.evaluateBlocking("function prnt() {var txt = $packedCode; return txt;}prnt();")
    }
}