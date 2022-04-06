package knf.hydra.module.test.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.data.GallerySource
import knf.hydra.core.models.data.VideoSource
import knf.hydra.core.models.data.WebSource
import knf.hydra.module.test.extras.Repository
import knf.tools.bypass.startBypass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainTest: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBypass(5547,"https://animeflv.net",true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5547){
            val bypass = BypassModel("https://animeflv.net",data?.getStringExtra("user_agent")?:"",data?.getStringExtra("cookies")?:"")
            lifecycleScope.launch(Dispatchers.IO) {
                Repository().sourceData("https://www3.animeflv.net/ver/isekai-maou-to-shoukan-shoujo-no-dorei-majutsu-o-5",bypass).collect { sourceData ->
                    if (sourceData != null){
                        when(sourceData){
                            is VideoSource -> {
                                sourceData.items.forEach {
                                    Log.e("Source", it.link)
                                }
                            }
                            is GallerySource -> {
                                sourceData.items.forEach {
                                    Log.e("Source", it.link)
                                }
                            }
                            is WebSource -> {
                                sourceData.items.forEach {
                                    Log.e("Source", it.link)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}