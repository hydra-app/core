package knf.hydra.core.tools

import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object ModulePreferences {
    private lateinit var manager: ModulePreferenceDao
    lateinit var pkg: String

    fun init(db: ModulePreferenceDB) {
        manager = db.dao()
    }

    private fun createKey(key: String) = "${pkg.replace(".","|")}:$key"

    suspend fun <T> getPreference(key: String, default: T): T = withContext(Dispatchers.IO) { (manager.findPreference(createKey(key))?.value?.toType(default) as? T) ?: default }
    suspend fun <T> getPreferenceOrNull(key: String): T? = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as? T }
    suspend fun <T> getPreferenceOrThrow(key: String): T = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as T }
    suspend fun <T> setPreference(key: String, value: T?) = withContext(Dispatchers.IO) {
        when (value) {
            is String -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_STRING))
            is Int -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_INT))
            is Boolean -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_BOOLEAN))
            is Long -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_LONG))
            is Float -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_FLOAT))
            else -> if (value == null){
                manager.insert(ModulePreference(createKey(key), value?.toString(), ModulePreference.TYPE_STRING))
            }
        }
    }

    fun <T> getPreferenceBlocking(key: String, default: T): T = runBlocking { getPreference(key, default) }
    fun <T> getPreferenceOrNullBlocking(key: String): T? = runBlocking { getPreferenceOrNull(key) }
    fun <T> getPreferenceOrThrowBlocking(key: String): T = runBlocking { getPreferenceOrThrow(key) }
    fun <T> setPreferenceBlocking(key: String, value: T?) = runBlocking { setPreference(key, value) }

    private fun <T> String.toType(type: T): Any? {
        return when (type) {
            is String -> this
            is Int -> this.toInt()
            is Boolean -> this.toBoolean()
            is Long -> this.toLong()
            is Float -> this.toFloat()
            else -> null
        }
    }

    class Sealed(private val pkg: String) {
        private fun createKey(key: String) = "${pkg.replace(".","|")}:$key"

        suspend fun <T> getPreference(key: String, default: T): T = withContext(Dispatchers.IO) { (manager.findPreference(createKey(key))?.value?.toType(default) as? T) ?: default }
        suspend fun <T> getPreferenceOrNull(key: String): T? = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as? T }
        suspend fun <T> getPreferenceOrThrow(key: String): T = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as T }
        suspend fun <T> setPreference(key: String, value: T?) = withContext(Dispatchers.IO) {
            when (value) {
                is String -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_STRING))
                is Int -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_INT))
                is Boolean -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_BOOLEAN))
                is Long -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_LONG))
                is Float -> manager.insert(ModulePreference(createKey(key), value.toString(), ModulePreference.TYPE_FLOAT))
                else -> if (value == null){
                    manager.insert(ModulePreference(createKey(key), value?.toString(), ModulePreference.TYPE_STRING))
                }
            }
        }

        private fun <T> String.toType(type: T): Any? {
            return when (type) {
                is String -> this
                is Int -> this.toInt()
                is Boolean -> this.toBoolean()
                is Long -> this.toLong()
                is Float -> this.toFloat()
                else -> null
            }
        }
    }
}

@Database(entities = [ModulePreference::class], version = 1)
abstract class ModulePreferenceDB: RoomDatabase() {
    abstract fun dao(): ModulePreferenceDao
}

@Dao
interface ModulePreferenceDao{
    @Query("SELECT * FROM modulepreference WHERE `key` = :key")
    fun findPreference(key: String): ModulePreference?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preference: ModulePreference)
}

@Entity
data class ModulePreference(@PrimaryKey val key: String, val value: String?, val type: Int){
    fun asType(): Any?{
        return when(type) {
            0 -> value
            1 -> value?.toInt()
            2 -> value?.toBoolean()
            3 -> value?.toLong()
            4 -> value?.toFloat()
            else -> null
        }
    }
    companion object{
        const val TYPE_STRING = 0
        const val TYPE_INT = 1
        const val TYPE_BOOLEAN = 2
        const val TYPE_LONG = 3
        const val TYPE_FLOAT = 4
    }
}