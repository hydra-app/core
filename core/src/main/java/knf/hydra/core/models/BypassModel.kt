package knf.hydra.core.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BypassModel(@PrimaryKey val baseUrl: String, val userAgent: String, val cookies: String){
    fun asMap() = mapOf("User-Agent" to userAgent, "Cookie" to cookies)

    companion object{
        fun generateDefault(url: String) = BypassModel(url, System.getProperty("http.agent"), "")
    }
}