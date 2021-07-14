package knf.hydra.core

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.reflect.KClass

abstract class HeadModule {
    private var internalContext: Context? = null
    val coreVersion = 1
    abstract val moduleVersionCode: Int
    abstract val moduleVersionName: String
    abstract val baseUrl: String
    abstract val moduleName: String
    abstract val iconRes: Int
    abstract val dataRepository: HeadRepository
    abstract val config: HeadConfig
    open suspend fun onModuleInitialize(){}
    suspend fun initModule(context: Context){
        internalContext = context
        withTimeoutOrNull(1000){
            try {
                onModuleInitialize()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        internalContext = null
    }
    fun <T: RoomDatabase>createRoomDatabase(name: String, clazz: KClass<T>): RoomDatabase.Builder<T> {
        internalContext?:throw IllegalStateException("Can't call this method outside onModuleInitialize")
        return Room.databaseBuilder(internalContext!!.applicationContext, clazz.java, "${moduleName.hashCode()}-$name")
    }
}