package knf.hydra.modules

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import dalvik.system.PathClassLoader
import knf.hydra.core.HeadModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ModuleLoader {
    val minLibVersion = 1
    private val modulesLive = MutableLiveData<List<ModuleInfo>>()
    private val currentModule = MutableLiveData<ModuleInfo?>()

    @SuppressLint("UseCompatLoadingForDrawables")
    fun loadModules(context: Context) {
        GlobalScope.launch(Dispatchers.IO) {
            val manager = context.packageManager
            val installed =
                manager.getInstalledPackages(PackageManager.GET_CONFIGURATIONS or PackageManager.GET_SIGNATURES)
            val currentModules = installed.filter { isModule(it) }
            val modules = currentModules.mapNotNull { pkgInfo ->
                try {
                    val name = manager.getApplicationLabel(pkgInfo.applicationInfo).toString()
                    val appInfo = manager.getApplicationInfo(
                        pkgInfo.packageName,
                        PackageManager.GET_META_DATA
                    )
                    val classLoader = PathClassLoader(appInfo.sourceDir, null, context.classLoader)
                    val module = Class.forName("${pkgInfo.packageName}.Module", false, classLoader)
                        .newInstance()
                    if (module is HeadModule) {
                        val resources = manager.getResourcesForApplication(pkgInfo.applicationInfo)
                        ModuleInfo(
                            name,
                            pkgInfo.packageName,
                            module.moduleVersionCode,
                            resources.getDrawable(module.iconRes),
                            module
                        )
                    } else null
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            withContext(Dispatchers.Main) { modulesLive.value = modules }
        }
    }

    private fun isModule(pkg: PackageInfo): Boolean {
        return pkg.packageName.startsWith("knf.hydra.module") && pkg.reqFeatures.orEmpty()
            .any { it.name == "hydra.module" }
    }
}

data class ModuleInfo(
    val name: String,
    val pkg: String,
    val libVersion: Int,
    val icon: Drawable,
    val module: HeadModule
) {
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ModuleInfo>() {
            override fun areItemsTheSame(oldItem: ModuleInfo, newItem: ModuleInfo): Boolean =
                oldItem.pkg == newItem.pkg

            override fun areContentsTheSame(oldItem: ModuleInfo, newItem: ModuleInfo): Boolean =
                newItem.name == oldItem.name && newItem.module.iconRes == oldItem.module.iconRes
        }
    }
}