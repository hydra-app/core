package knf.hydra.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the bypass data for a base link.
 *
 * @property baseUrl The base url for this bypass
 * @property userAgent The User Agent used to generate the bypass
 * @property cookies The cookies string for the bypass
 */
@Entity
data class BypassModel(@PrimaryKey val baseUrl: String, val userAgent: String, val cookies: String){

    /**
     * Get the data in a headers map with optional extra cookies.
     *
     * @param extraCookies Optional extra cookies to add to the cookies string
     */
    fun asMap(extraCookies: Map<String,String> = emptyMap()) = mapOf("User-Agent" to userAgent, "Cookie" to cookies.let { c ->
        if (extraCookies.isEmpty())
            c
        else {
            StringBuilder().apply{
                if (cookies.endsWith(";"))
                    append(cookies.substringBeforeLast(";"))
                else
                    append(cookies)
                extraCookies.forEach {
                    append("; ")
                    append(it.key)
                    append("=")
                    append(it.value)
                }
            }.toString()
        }
    })

    companion object{
        fun generateDefault(url: String) = BypassModel(url, System.getProperty("http.agent")?:"default", "")
    }
}