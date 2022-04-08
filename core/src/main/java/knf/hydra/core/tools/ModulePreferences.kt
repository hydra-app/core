/*
 * Created by @UnbarredStream on 08/04/22 18:10
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 18:08
 */

package knf.hydra.core.tools

import androidx.annotation.RestrictTo
import androidx.room.*
import knf.hydra.core.HeadConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/** Object used to access the module preferences defined in [HeadConfig.settingsPage] */
object ModulePreferences {
    private lateinit var manager: ModulePreferenceDao
    private lateinit var pkg: String
    /** @suppress */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    fun init(db: ModulePreferenceDB, currentPkg: String?) {
        manager = db.dao()
        pkg = currentPkg ?: "dummy"
    }
    private fun createKey(key: String) = "${pkg.replace(".","|")}:$key"

    /**
     * Get preference value by [key] or [default] suspending coroutine
     *
     * @param key Preference key
     * @param default Default value
     * @return The value of the preference or [default] if not found or is null
     */
    suspend fun <T> getPreference(key: String, default: T): T = withContext(Dispatchers.IO) { (manager.findPreference(createKey(key))?.value?.toType(default) as? T) ?: default }
    /**
     * Get preference value by [key] or null suspending coroutine
     *
     * @param key Preference key
     * @return The value of the preference or null if not found
     */
    suspend fun <T> getPreferenceOrNull(key: String): T? = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as? T }
    /**
     * Get preference value by [key] or throw suspending coroutine
     *
     * @param key Preference key
     * @return The value of the preference or throw if not found
     */
    suspend fun <T> getPreferenceOrThrow(key: String): T = withContext(Dispatchers.IO) { manager.findPreference(createKey(key)).also { if (it == null) throwNotFound(key)  }?.asType() as T }
    /**
     * Set preference value by [key] suspending coroutine
     *
     * @param key Preference key
     * @param value The value of the preference
     */
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
    /**
     * Get preference value by [key] or [default] blocking the thread
     *
     * @param key Preference key
     * @param default Default value
     * @return The value of the preference or [default] if not found or is null
     */
    fun <T> getPreferenceBlocking(key: String, default: T): T = runBlocking { getPreference(key, default) }
    /**
     * Get preference value by [key] or null blocking the thread
     *
     * @param key Preference key
     * @return The value of the preference or null if not found
     */
    fun <T> getPreferenceOrNullBlocking(key: String): T? = runBlocking { getPreferenceOrNull(key) }
    /**
     * Get preference value by [key] or throw blocking the thread
     *
     * @param key Preference key
     * @return The value of the preference or throw if not found
     */
    fun <T> getPreferenceOrThrowBlocking(key: String): T = runBlocking { getPreferenceOrThrow(key) }
    /**
     * Set preference value by [key] blocking the thread
     *
     * @param key Preference key
     * @param value The value of the preference
     */
    fun <T> setPreferenceBlocking(key: String, value: T?) = runBlocking { setPreference(key, value) }

    private fun throwNotFound(key: String): Nothing = throw PreferenceNotFoundException(key)

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

    /** @suppress */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    class Sealed(private val pkg: String) {
        private fun createKey(key: String) = "${pkg.replace(".","|")}:$key"

        /** @suppress */
        suspend fun <T> getPreference(key: String, default: T): T = withContext(Dispatchers.IO) { (manager.findPreference(createKey(key))?.value?.toType(default) as? T) ?: default }
        /** @suppress */
        suspend fun <T> getPreferenceOrNull(key: String): T? = withContext(Dispatchers.IO) { manager.findPreference(createKey(key))?.asType() as? T }
        /** @suppress */
        suspend fun <T> getPreferenceOrThrow(key: String): T = withContext(Dispatchers.IO) { manager.findPreference(createKey(key)).also { if (it == null) throwNotFound(key)  }?.asType() as T }
        /** @suppress */
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

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class PreferenceNotFoundException(key: String): Exception("Preference with key $key not found or is null")

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
@Database(entities = [ModulePreference::class], version = 1, exportSchema = false)
abstract class ModulePreferenceDB: RoomDatabase() {
    abstract fun dao(): ModulePreferenceDao
}

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
@Dao
interface ModulePreferenceDao{
    /** @suppress */
    @Query("SELECT * FROM modulepreference WHERE `key` = :key")
    fun findPreference(key: String): ModulePreference?
    /** @suppress */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preference: ModulePreference)
}

/** @suppress */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
@Entity
data class ModulePreference(@PrimaryKey val key: String, val value: String?, val type: Int){
    /** @suppress */
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