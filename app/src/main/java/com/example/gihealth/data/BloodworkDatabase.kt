package com.example.gihealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BloodworkEntity::class],
    version = 1
)
abstract class BloodworkDatabase : RoomDatabase() {

    abstract fun bloodworkDao(): BloodworkDao

    companion object {
        @Volatile
        internal var INSTANCE: BloodworkDatabase? = null

        fun getDatabase(context: Context): BloodworkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloodworkDatabase::class.java,
                    "bloodwork_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}