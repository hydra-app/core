/*
 * Created by @UnbarredStream on 08/04/22 19:35
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 08/04/22 19:33
 */

package knf.hydra.core

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.reflect.KClass


/**
 * Main class to crate a Module for [Hydra](https://knf-hydra.app/)
 *
 * **Don't modify this code**, the main app and the module need to have exactly the
 * same library implementation in order to work, in the same way you can only implement an specific
 * list of libraries, the main app needs to have a copy of that library in order to work.
 *
 * TODO: Add compatible libraries list
 *
 * The modules are limited in the permission they can declare, if the main app detects any other
 * permission besides those, the module will be ignored, the admitted permissions are:
 * - [android.permission.INTERNET]
 * - [android.permission.ACCESS_WIFI_STATE]
 *
 * To create a module you need to create a new Application project, once created you need to create
 * a class named **Module** that extends [HeadModule], because the core is based in abstract
 * implementation the minimum required functions needs to be overridden, the other overridable
 * functions are optional (i.e., the main app will try to use the functions, but if the module
 * doesn't implement them or any of them fail/crash it will adapt the UI to hide the related elements).
 *
 * The name of the application doesn't need any requirements, but it can be a little descriptive
 * for the users (e.g., Hydra Module: My module name) in case of a manual uninstall, the app won't
 * use this name internally, instead it will use the name provided in the [moduleName] implementation.
 *
 * <b>Sample usage</b>
 *
 * ```kotlin
 * class Module: HeadModule() {
 *      override val moduleVersionCode: Int = BuildConfig.VERSION_CODE
 *      override val moduleVersionName: String = BuildConfig.VERSION_NAME
 *      override val baseUrl: String = "https://empty.com"
 *      override val moduleName: String = "Test Module"
 *      override val dataRepository: HeadRepository = Repository()
 *      override val config: HeadConfig = TestConfig()
 *  }
 * ```
 */
abstract class HeadModule {
    /** @suppress */
    private var internalContext: Context? = null

    /** The module version code, usually **BuildConfig.VERSION_CODE** for easier implementation */
    abstract val moduleVersionCode: Int

    /** The module version name, usually **BuildConfig.VERSION_NAME** for easier implementation */
    abstract val moduleVersionName: String

    /** Base Url of the module to check for Cloudflare anti DDoS, in case of  protected url the app
     * will try to bypass it, in case of a custom behaviour is needed for the bypass use the
     * **BypassBehavior** in [config] implementation.
     *
     * @see knf.hydra.core.models.data.BypassBehavior
     */
    abstract val baseUrl: String

    /** The module name, it will be used internally to list the Module */
    abstract val moduleName: String

    /**
     * Data repository for the module, all the data required by the Main app in order to show content
     * comes from here.
     *
     * @see knf.hydra.core.HeadRepository
     */
    abstract val dataRepository: HeadRepository

    /**
     * The configuration for the module, here you can enable and modify the behaviour of some
     * features in the module.
     *
     * @see knf.hydra.core.HeadConfig
     */
    abstract val config: HeadConfig

    /**
     * This method is called the first time the module is initialized, here you can get/create
     * internal databases using [createRoomDatabase].
     */
    open suspend fun onModuleInitialize(){}

    /** @suppress */
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

    /**
     * Create or get a RoomDatabase, it will be created in the Main app storage and will persist if
     * the module is uninstalled, we suggest to put the database reference inside an object for
     * easier access through the module, this method can only be used inside of [onModuleInitialize].
     *
     * @param name The name of the database, the package name of the Module is used to create an unique name
     * @param clazz The class of the Database
     * @return The reference of the created/gotten Database
     * @see [RoomDatabase]
     */
    fun <T: RoomDatabase>createRoomDatabase(name: String, clazz: KClass<T>): RoomDatabase.Builder<T> {
        internalContext?:throw IllegalStateException("Can't call this method outside onModuleInitialize time limit")
        return Room.databaseBuilder(internalContext!!.applicationContext, clazz.java, "${internalContext!!.packageName.replace(".", "|")}-$name")
    }
}