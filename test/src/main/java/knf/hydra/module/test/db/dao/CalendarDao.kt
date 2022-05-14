/*
 * Created by @UnbarredStream on 13/05/22 19:01
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 13/05/22 18:59
 */

package knf.hydra.module.test.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import knf.hydra.module.test.models.TestDirectoryModel

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendar WHERE releaseDay = :day ORDER BY name")
    suspend fun getByDay(day: Int): List<TestDirectoryModel>

    @Query("SELECT COUNT(*) FROM calendar")
    fun countAll(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<TestDirectoryModel>)

    @Query("DELETE FROM calendar")
    fun nuke()
}