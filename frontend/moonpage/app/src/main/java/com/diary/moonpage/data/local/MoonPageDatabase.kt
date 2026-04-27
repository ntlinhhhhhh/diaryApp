package com.diary.moonpage.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diary.moonpage.data.local.dao.DailyLogDao
import com.diary.moonpage.data.local.entity.DailyLogEntity

@Database(entities = [DailyLogEntity::class], version = 1, exportSchema = false)
abstract class MoonPageDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        const val DATABASE_NAME = "moon_page_db"
    }
}
