package knf.hydra.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BypassModel(@PrimaryKey val baseUrl: String, val userAgent: String, val cookies: String){
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