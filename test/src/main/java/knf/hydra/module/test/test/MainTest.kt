/*
 * Created by @UnbarredStream on 19/06/22 13:39
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 19/06/22 13:39
 */

package knf.hydra.module.test.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import knf.hydra.core.models.BypassModel
import knf.hydra.core.models.ContentItemMin
import knf.hydra.module.test.extras.Repository
import knf.tools.bypass.startBypass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBypass(5547, "https://animeflv.net", true)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5547) {
            val bypass = BypassModel("https://animeflv.net", data?.getStringExtra("user_agent") ?: "", data?.getStringExtra("cookies") ?: "")
            lifecycleScope.launch(Dispatchers.IO) {
                val sourceData = Repository().sourceData(ContentItemMin(link = "https://www3.animeflv.net/ver/isekai-maou-to-shoukan-shoujo-no-dorei-majutsu-o-5"), bypass)
                if (sourceData != null) {
                    sourceData.itemsFlow.collect {
                        it.forEach {
                            Log.e("Source", it.link)
                        }
                    }
                }
            }
        }
    }
}