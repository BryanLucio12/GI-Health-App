package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WellBeingEntity::class], version = 3)
abstract class WellBeingDatabase : RoomDatabase() {
    abstract fun wellBeingDao(): WellBeingDao
    companion object {
        @Volatile
        private var INSTANCE: WellBeingDatabase? = null
        fun getDatabase(context: Context): WellBeingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WellBeingDatabase::class.java,
                    "wellbeing_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}