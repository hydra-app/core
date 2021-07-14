package knf.hydra.module.test.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import knf.hydra.module.test.db.dao.CalendarDao
import knf.hydra.module.test.models.TestDirectoryModel

@Database(entities = [TestDirectoryModel::class], version = 1)
@TypeConverters(RoomTypeConverters::class)
abstract class DB : RoomDatabase() {

    abstract fun calendarDao(): CalendarDao

    companion object {
        fun isActive() = ::INSTANCE.isInitialized
        lateinit var INSTANCE: DB

        fun start(db: RoomDatabase): Boolean {
            return if (db is DB) {
                INSTANCE = db
                true
            }else{
                false
            }
        }
    }
}