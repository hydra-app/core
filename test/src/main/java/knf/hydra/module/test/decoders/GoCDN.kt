package knf.hydra.module.test.decoders

import android.content.Context
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.data.DecodeResult
import knf.hydra.core.models.data.Option
import knf.hydra.core.models.data.SourceItem
import knf.hydra.core.models.data.VideoDecoder
import org.json.JSONObject
import java.net.URL

class GoCDN: VideoDecoder() {
    override fun canDecode(link: String): Boolean = link.contains("gocdn")

    override suspend fun decode(context: Context, item: SourceItem, bypassModel: BypassModel): DecodeResult {
        return try {
            val json = JSONObject(URL("https://streamium.xyz/gocdn.php?v=${item.link.substringAfterLast("#")}").readText())
            DecodeResult.Success(listOf(Option(json.getString("file"))))
        }catch (e:Exception){
            e.printStackTrace()
            DecodeResult.Failed()
        }
    }
}